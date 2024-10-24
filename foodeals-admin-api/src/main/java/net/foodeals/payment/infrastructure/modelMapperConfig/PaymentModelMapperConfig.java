package net.foodeals.payment.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.enums.TransactionStatus;
import net.foodeals.order.domain.enums.TransactionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerI;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PaymentModelMapperConfig {

    private final ModelMapper modelMapper;
    private final OrderService orderService;
    private final ContractService contractService;
    private final CommissionService commissionService;
    private final OrganizationEntityService organizationEntityService;
    private final SubEntityService subEntityService;

    @PostConstruct
    @Transactional
    public void paymentModelMapperConfig() {
        modelMapper.addConverter(mappingContext -> {
            PartnerCommissions partnerCommissions = mappingContext.getSource();
            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(partnerCommissions.getPartner().getName(), partnerCommissions.getPartner().getAvatarPath());
            String organizationName = !partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) ? partnerCommissions.getPartner().getName() : ((SubEntity) partnerCommissions.getPartner()).getOrganizationEntity().getName();
            UUID organizationId = !partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) ? partnerCommissions.getPartner().getId() : ((SubEntity) partnerCommissions.getPartner()).getOrganizationEntity().getId();
            Commission commission = this.commissionService.getCommissionByPartnerName(organizationName);
            SimpleDateFormat formatter = new SimpleDateFormat("M/yyyy");
            List<Order> orderList = this.orderService.findByOfferPublisherInfoIdAndDate(partnerCommissions.getPartner().getId(), partnerCommissions.getDate());
            System.out.println(orderList);
            System.out.println("--");
            List<Transaction> transactions = orderList.stream()
                    .flatMap(order -> order.getTransactions().stream())
                    .collect(Collectors.toList());
            BigDecimal paymentsWithCash = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                    .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paymentsWithCard = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                    .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * paymentsWithCard.doubleValue()  + ((Double)(commission.getCash().doubleValue() / 100)) * paymentsWithCash.doubleValue();
            Double difference = (paymentsWithCard.doubleValue() - commissionTotal);
            Double toPay = difference < 0 ? 0 : difference;
            Double toReceive = difference < 0 ? difference : 0;
            BigDecimal totalAmount = paymentsWithCard.add(paymentsWithCash);
            boolean payable = (partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) && partnerCommissions.getPartner().commissionPayedBySubEntities() == false) ? false : true;
            return new CommissionPaymentDto(partnerCommissions.getId(), partnerCommissions.getPartner().getId(),  organizationId,formatter.format(partnerCommissions.getDate()), partnerInfoDto, partnerCommissions.getPartner().getPartnerType(), totalAmount.doubleValue(), commissionTotal, toPay, toReceive, partnerCommissions.getPaymentStatus(), payable, partnerCommissions.getPartner().commissionPayedBySubEntities());
        }, PartnerCommissions.class, CommissionPaymentDto.class);
        modelMapper.addMappings(new PropertyMap<Subscription, SubscriptionPaymentDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getId());
                map(source.getSubscriptionStatus(), destination.getSubscriptionStatus());
            }
        });

        modelMapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();

            PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().name(organizationEntity.getName())
                    .avatarPath(organizationEntity.getAvatarPath())
                    .build();
            return CommissionPaymentDto.builder()
                    .entityId(organizationEntity.getId())
                    .organizationId(organizationEntity.getId())
                    .partnerInfoDto(partnerInfoDto)
                    .partnerType(organizationEntity.getPartnerType())
                    .build();
        }, OrganizationEntity.class, CommissionPaymentDto.class);
    }
}
