package net.foodeals.payment.application.services;

import jakarta.transaction.Transactional;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.application.service.DeadlinesService;
import net.foodeals.contract.application.service.SubscriptionService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.DeadlinesDto;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.domain.entities.Payment;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final CommissionService commissionService;
    private final SubscriptionService subscriptionService;
    private final DeadlinesService deadlinesService;


    public PaymentService(PaymentRepository paymentRepository, ModelMapper modelMapper, CommissionService commissionService, SubscriptionService subscriptionService, DeadlinesService deadlinesService) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.commissionService = commissionService;
        this.subscriptionService = subscriptionService;
        this.deadlinesService = deadlinesService;
    }

    public Page<Payment> getCommissionPayments(Pageable page) {
        return this.paymentRepository.findAll(page);
    }

    @Transactional
    public CommissionPaymentDto toCommissionPaymentDto(Payment payment) {
        CommissionPaymentDto paymentDto = this.modelMapper.map(payment, CommissionPaymentDto.class);
        OrganizationEntity organizationEntity = payment.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? payment.getOrganizationEntity()
                    : payment.getSubEntity().getOrganizationEntity();

        Commission commission = this.commissionService.getCommissionByPartnerName(organizationEntity.getName());

        Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * payment.getPaymentsWithCard() + ((Double)(commission.getCash().doubleValue() / 100)) * payment.getPaymentsWithCash();
        Double difference = payment.getPaymentsWithCard() - commissionTotal;
        Double toPay = difference < 0 ? 0 : difference;
        paymentDto.setToPay(toPay.toString());
        Double toReceive = difference < 0 ? difference : 0;
        paymentDto.setToReceive(toReceive.toString());
        paymentDto.setFoodealsCommission(commissionTotal.toString());
        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
        paymentDto.setPartnerType(payment.getPartnerType());
        paymentDto.setPartnerInfoDto(partnerInfoDto);
        return paymentDto;
    }

    public Page<Subscription> getSubscriptionPayments(Pageable page) {
        return this.subscriptionService.findAll(page);
    }

    public SubscriptionPaymentDto toSubscriptionPaymentDto(Subscription subscription) {
        SubscriptionPaymentDto subscriptionPaymentDto = this.modelMapper.map(subscription, SubscriptionPaymentDto.class);
        List<Deadlines> deadlines = subscription.getDeadlines();
        List<DeadlinesDto> deadlinesDtos =  deadlines.stream().map(this.deadlinesService::toDeadlineDto).toList();

        List<String> solutions = subscription.getSolutionContracts().stream().map(solutionContract -> solutionContract.getSolution().getName()).toList();

        subscriptionPaymentDto.setSolutions(solutions);

        subscriptionPaymentDto.setDeadlinesDtoList(deadlinesDtos);
//        String partnerName = subscription.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? subscription.getOrganizationEntity().getName()
//                                        : subscription.getSubEntity().getName();
        String partnerName = "test";
//        String avatarPath = subscription.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? subscription.getOrganizationEntity().getAvatarPath()
//                : subscription.getSubEntity().getAvatarPath();

        String avatarPath = "12";

        subscriptionPaymentDto.setTotalAmount(subscription.getAmount().amount());
        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().name(partnerName)
                .avatarPath(avatarPath)
                .build();
        subscriptionPaymentDto.setPartnerType(subscription.getPartnerType());
        subscriptionPaymentDto.setPartnerInfoDto(partnerInfoDto);
        return subscriptionPaymentDto;
    }
}
