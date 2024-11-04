package net.foodeals.payment.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Deadlines;
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
import net.foodeals.payment.application.dto.response.*;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerI;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Currency;
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
            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(partnerCommissions.getPartner().getId(), partnerCommissions.getPartner().getName(), partnerCommissions.getPartner().getAvatarPath());
            UUID organizationId = !partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) ? partnerCommissions.getPartner().getId() : ((SubEntity) partnerCommissions.getPartner()).getOrganizationEntity().getId();
            Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
            SimpleDateFormat formatter = new SimpleDateFormat("M/yyyy");
            List<Order> orderList = this.orderService.findByOfferPublisherInfoIdAndDate(partnerCommissions.getPartner().getId(), partnerCommissions.getDate());
            List<Transaction> transactions = orderList.stream()
                    .flatMap(order -> order.getTransactions().stream())
                    .collect(Collectors.toList());
            Currency mad = Currency.getInstance("MAD");
            Price paymentsWithCash = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                    .map(transaction -> transaction.getPrice()).reduce(Price.ZERO(mad), Price::add);
            Price paymentsWithCard = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                    .map(transaction -> transaction.getPrice()).reduce(Price.ZERO(mad), Price::add);
            Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * paymentsWithCard.amount().doubleValue()  + ((Double)(commission.getCash().doubleValue() / 100)) * paymentsWithCash.amount().doubleValue();
            Double difference = (paymentsWithCard.amount().doubleValue() - commissionTotal);
            Double toPay = difference < 0 ? 0 : difference;
            Double toReceive = difference < 0 ? Math.abs(difference) : 0;
            Price totalAmount = Price.add(paymentsWithCash, paymentsWithCard);
            totalAmount = new Price(totalAmount.amount().setScale(3, BigDecimal.ROUND_HALF_UP), mad);
            boolean payable = (partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) && partnerCommissions.getPartner().commissionPayedBySubEntities() == false) ? false : true;
            return new CommissionPaymentDto(partnerCommissions.getId(), partnerCommissions.getPartner().getId(),  organizationId,formatter.format(partnerCommissions.getDate()), partnerInfoDto, partnerCommissions.getPartner().getPartnerType(), totalAmount, new Price(new BigDecimal(commissionTotal).setScale(3, RoundingMode.HALF_UP), mad), new Price(new BigDecimal(toPay).setScale(3, RoundingMode.HALF_UP), mad), new Price(new BigDecimal(toReceive).setScale(3, RoundingMode.HALF_UP), mad), partnerCommissions.getPaymentStatus(), payable, partnerCommissions.getPartner().commissionPayedBySubEntities());
        }, PartnerCommissions.class, CommissionPaymentDto.class);

        modelMapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();

            PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().id(organizationEntity.getId()).name(organizationEntity.getName())
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
