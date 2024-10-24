package net.foodeals.payment.application.services.impl;//package net.foodeals.payment.application.services;
//
//import jakarta.transaction.Transactional;
//import net.foodeals.contract.application.service.CommissionService;
//import net.foodeals.contract.application.service.DeadlinesService;
//import net.foodeals.contract.application.service.SubscriptionService;
//import net.foodeals.contract.domain.entities.Commission;
//import net.foodeals.contract.domain.entities.Deadlines;
//import net.foodeals.contract.domain.entities.Subscription;
//import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
//import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
//import net.foodeals.payment.application.dto.response.DeadlinesDto;
//import net.foodeals.payment.application.dto.response.PartnerInfoDto;
//import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
//import net.foodeals.payment.domain.entities.Payment;
//import net.foodeals.payment.domain.entities.Enum.PartnerType;
//import net.foodeals.payment.domain.repository.PaymentRepository;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class PaymentServiceImpl {
//    private final PaymentRepository paymentRepository;
//    private final ModelMapper modelMapper;
//    private final CommissionService commissionService;
//    private final SubscriptionService subscriptionService;
//    private final DeadlinesService deadlinesService;
//
//
//    public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper modelMapper, CommissionService commissionService, SubscriptionService subscriptionService, DeadlinesService deadlinesService) {
//        this.paymentRepository = paymentRepository;
//        this.modelMapper = modelMapper;
//        this.commissionService = commissionService;
//        this.subscriptionService = subscriptionService;
//        this.deadlinesService = deadlinesService;
//    }
//
//    public Page<Payment> getCommissionPayments(Pageable page) {
//        return this.paymentRepository.findAll(page);
//    }
//
//    @Transactional
//    public CommissionPaymentDto toCommissionPaymentDto(Payment payment) {
//        CommissionPaymentDto paymentDto = this.modelMapper.map(payment, CommissionPaymentDto.class);
//        OrganizationEntity organizationEntity = payment.getPartnerType() == PartnerType.PARTNER ? payment.getOrganizationEntity()
//                    : payment.getSubEntity().getOrganizationEntity();
//
//        Commission commission = this.commissionService.getCommissionByPartnerName(organizationEntity.getName());
//
//        Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * payment.getPaymentsWithCard() + ((Double)(commission.getCash().doubleValue() / 100)) * payment.getPaymentsWithCash();
//        Double difference = payment.getPaymentsWithCard() - commissionTotal;
//        Double toPay = difference < 0 ? 0 : difference;
//        paymentDto.setToPay(toPay.toString());
//        Double toReceive = difference < 0 ? difference : 0;
//        paymentDto.setToReceive(toReceive.toString());
//        paymentDto.setFoodealsCommission(commissionTotal.toString());
//        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
//        paymentDto.setPartnerType(payment.getPartnerType());
//        paymentDto.setPartnerInfoDto(partnerInfoDto);
//        return paymentDto;
//    }
//
//    public Page<Subscription> getSubscriptionPayments(Pageable page) {
//        return this.subscriptionService.findAll(page);
//    }
//
//    public SubscriptionPaymentDto toSubscriptionPaymentDto(Subscription subscription) {
//        SubscriptionPaymentDto subscriptionPaymentDto = this.modelMapper.map(subscription, SubscriptionPaymentDto.class);
//        List<Deadlines> deadlines = subscription.getDeadlines();
//        List<DeadlinesDto> deadlinesDtos =  deadlines.stream().map(this.deadlinesService::toDeadlineDto).toList();
//
//        List<String> solutions = subscription.getSolutionContracts().stream().map(solutionContract -> solutionContract.getSolution().getName()).toList();
//
//        subscriptionPaymentDto.setSolutions(solutions);
//
//        subscriptionPaymentDto.setDeadlinesDtoList(deadlinesDtos);
////        String partnerName = subscription.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? subscription.getOrganizationEntity().getName()
////                                        : subscription.getSubEntity().getName();
//        String partnerName = "test";
////        String avatarPath = subscription.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? subscription.getOrganizationEntity().getAvatarPath()
////                : subscription.getSubEntity().getAvatarPath();
//
//        String avatarPath = "12";
//
//        subscriptionPaymentDto.setTotalAmount(subscription.getAmount().amount());
//        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().name(partnerName)
//                .avatarPath(avatarPath)
//                .build();
//        subscriptionPaymentDto.setPartnerType(subscription.getPartnerType());
//        subscriptionPaymentDto.setPartnerInfoDto(partnerInfoDto);
//        return subscriptionPaymentDto;
//    }
//}

import jakarta.transaction.Transactional;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.application.service.DeadlinesService;
import net.foodeals.contract.application.service.SubscriptionService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.offer.domain.enums.PublisherType;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.enums.TransactionStatus;
import net.foodeals.order.domain.enums.TransactionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.response.*;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PartnerCommissionsRepository partnerCommissionsRepository;
    private final ModelMapper modelMapper;
    private final CommissionService commissionService;
    private final SubscriptionService subscriptionService;
    private final DeadlinesService deadlinesService;
    private final Map<String, PaymentProcessor> processors;
    private final SubEntityService subEntityService;
    private final OrganizationEntityService organizationEntityService;
    private final OrderService orderService;

    public PaymentServiceImpl(PartnerCommissionsRepository partnerCommissionsRepository, ModelMapper modelMapper, CommissionService commissionService, SubscriptionService subscriptionService, DeadlinesService deadlinesService, List<PaymentProcessor> processorList, SubEntityService subEntityService, OrganizationEntityService organizationEntityService, OrderService orderService) {
        this.partnerCommissionsRepository = partnerCommissionsRepository;
        this.modelMapper = modelMapper;
        this.commissionService = commissionService;
        this.subscriptionService = subscriptionService;
        this.deadlinesService = deadlinesService;
        this.processors = processorList.stream()
                .collect(Collectors.toMap(
                        processor -> processor.getClass().getSimpleName().replace("Processor", "").toLowerCase(),
                        Function.identity()
                ));
        this.subEntityService = subEntityService;
        this.organizationEntityService = organizationEntityService;
        this.orderService = orderService;
    }

    public Page<PartnerCommissions> getCommissionPayments(Pageable page, int year, int month) {
        return this.partnerCommissionsRepository.findCommissionsByDate(year, month, page);
    }

//    @Transactional
//    public CommissionPaymentDto toCommissionPaymentDto(List<PartnerCommissions> partnerCommissions) {
//        List<CommissionPaymentDto> commissionPaymentDtos  = partnerCommissions.this.modelMapper.map(partnerCommissions, CommissionPaymentDto.class);
////        CommissionPaymentDto paymentDto = this.modelMapper.map(partnerCommissions, CommissionPaymentDto.class);
////        OrganizationEntity organizationEntity = partnerCommissions.getPartnerType() == PartnerType.PARTNER ? partnerCommissions.getOrganizationEntity()
////                    : partnerCommissions.getSubEntity().getOrganizationEntity();
////
////        Commission commission = this.commissionService.getCommissionByPartnerName(organizationEntity.getName());
////
////        Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * partnerCommissions.getPaymentsWithCard() + ((Double)(commission.getCash().doubleValue() / 100)) * partnerCommissions.getPaymentsWithCash();
////        Double difference = partnerCommissions.getPaymentsWithCard() - commissionTotal;
////        Double toPay = difference < 0 ? 0 : difference;
////        paymentDto.setToPay(toPay.toString());
////        Double toReceive = difference < 0 ? difference : 0;
////        paymentDto.setToReceive(toReceive.toString());
////        paymentDto.setFoodealsCommission(commissionTotal.toString());
////        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
////        paymentDto.setPartnerType(partnerCommissions.getPartnerType());
////        paymentDto.setPartnerInfoDto(partnerInfoDto);
////        return paymentDto;
//    }

    @Transactional
    public Page<Subscription> getSubscriptionPayments(Pageable page, int year) {
        return this.subscriptionService.findByYear(year, page);
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

    @Override
    public PaymentResponse processPayment(PaymentRequest request, MultipartFile document) throws BadRequestException {
        PaymentProcessor processor = processors.get(request.paymentMethod().toLowerCase());
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + request.paymentMethod());
        }
        return processor.process(request, document);
    }

    @Override
    @Transactional
    public Page<CommissionPaymentDto> convertCommissionToDto(Page<PartnerCommissions> payments) {
        Set<OrganizationEntity> organizations = new HashSet<>();
        payments.map(payment -> {
            if (payment.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
                SubEntity subEntity = this.subEntityService.getEntityById(payment.getPartnerInfo().id());
                payment.setPartner(subEntity);
                if (subEntity.commissionPayedBySubEntities() == true)
                        organizations.add(subEntity.getOrganizationEntity());
            } else {
                OrganizationEntity organizationEntity = this.organizationEntityService.findById(payment.getPartnerInfo().id());
                payment.setPartner(organizationEntity);
            }
            return payment;
        });
        Page<CommissionPaymentDto> paymentDtos = payments.map(payment -> this.modelMapper.map(payment, CommissionPaymentDto.class));
        Page<CommissionPaymentDto> finalPaymentDtos = paymentDtos;
        List<CommissionPaymentDto> organizationsDto = organizations.stream().map(organization -> {
            CommissionPaymentDto commissionPaymentDto = this.modelMapper.map(organization, CommissionPaymentDto.class);
            List<CommissionPaymentDto> childs = finalPaymentDtos.stream().filter(payment -> payment.getPartnerType().equals(PartnerType.SUB_ENTITY) && payment.getOrganizationId().equals(organization.getId())).collect(Collectors.toList());
            Double totalAmount = childs.stream().map(payment -> payment.getTotalAmount()).collect(Collectors.summingDouble(Double::doubleValue));
            ;
            Double totalCommission = childs.stream()
                    .map(payment -> payment.getFoodealsCommission()).collect(Collectors.summingDouble(Double::doubleValue));
            Double toPay = childs.stream().map(payment -> payment.getToPay()).collect(Collectors.summingDouble(Double::doubleValue));
            Double toReceive = childs.stream().map(payment -> payment.getToReceive()).collect(Collectors.summingDouble(Double::doubleValue));
            commissionPaymentDto.setDate(childs.stream().findFirst().get().getDate());
            commissionPaymentDto.setTotalAmount(totalAmount);
            commissionPaymentDto.setFoodealsCommission(totalCommission);
            commissionPaymentDto.setToPay(toPay);
            commissionPaymentDto.setToReceive(toReceive);
            commissionPaymentDto.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
            commissionPaymentDto.setPayable(false);
            commissionPaymentDto.setCommissionPayedBySubEntities(true);
            childs.forEach(child -> {
                if (!child.getPaymentStatus().equals(PaymentStatus.VALIDATED_BY_BOTH)) {
                    commissionPaymentDto.setPaymentStatus(PaymentStatus.IN_VALID);
                }
            });
            return commissionPaymentDto;
        }).collect(Collectors.toList());
        Page<CommissionPaymentDto> finalPaymentDtos1 = paymentDtos;
        paymentDtos = paymentDtos.map(payment -> {
            if (payment.getPartnerType().equals(PartnerType.PARTNER_SB) && payment.isCommissionPayedBySubEntities() == false) {
                List<CommissionPaymentDto> childs = finalPaymentDtos1.stream().filter(c -> c.getPartnerType().equals(PartnerType.SUB_ENTITY) && c.getOrganizationId().equals(payment.getEntityId())).collect(Collectors.toList());
                Double totalAmount = childs.stream().map(p -> p.getTotalAmount()).collect(Collectors.summingDouble(Double::doubleValue));
                ;
                Double totalCommission = childs.stream()
                        .map(p2 -> p2.getFoodealsCommission()).collect(Collectors.summingDouble(Double::doubleValue));
                Double toPay = childs.stream().map(p3 -> p3.getToPay()).collect(Collectors.summingDouble(Double::doubleValue));
                Double toReceive = childs.stream().map(p4 -> p4.getToReceive()).collect(Collectors.summingDouble(Double::doubleValue));
                payment.setTotalAmount(totalAmount);
                payment.setFoodealsCommission(totalCommission);
                payment.setToPay(toPay);
                payment.setToReceive(toReceive);
                payment.setPayable(true);
            }
            if (payment.getPartnerType().equals(PartnerType.SUB_ENTITY) && payment.isCommissionPayedBySubEntities() == false) {
                payment.setPayable(false);
            }
            return payment;
        });
        organizationsDto.addAll(paymentDtos.stream().collect(Collectors.toList()));
        paymentDtos = new PageImpl<>(organizationsDto, paymentDtos.getPageable(), organizationsDto.size());
        return paymentDtos;
    }

    @Override
    @Transactional
    public void paySubscription(MultipartFile document, ReceiveDto receiveDto, UUID uuid) {
        Deadlines deadlines = this.deadlinesService.findById(uuid);

        if (deadlines == null) {
            throw  new ResourceNotFoundException("deadline not found with id " + uuid.toString());
        }

        deadlines.setStatus(DeadlineStatus.PAID);
        deadlines.setPayedAt(receiveDto.date());
        deadlines.setEmitterName(receiveDto.emitter());

        this.deadlinesService.save(deadlines);

        Subscription subscription = this.subscriptionService.findById(deadlines.getSubscription().getId());

        subscription.setSubscriptionStatus(SubscriptionStatus.VALID);
        subscription.getDeadlines().forEach(d -> {
            if (!d.getStatus().equals(DeadlineStatus.PAID)) {
                subscription.setSubscriptionStatus(SubscriptionStatus.IN_PROGRESS);
            }
        });
    }

    @Override
    @Transactional
    public Page<MonthlyOperationsDto> monthlyOperations(UUID id, int year, int month, Pageable page) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // month is 0-based in Calendar
        Date date = calendar.getTime();
        Page<Order> orders = this.orderService.findByOfferPublisherInfoIdAndDate(id, date, page);
        List<Order> filteredOrders  = orders.getContent().stream()
                .filter(o -> !o.getTransactions().isEmpty() &&
                        o.getTransactions().get(0).getStatus().equals(TransactionStatus.COMPLETED))
                .collect(Collectors.toList());
        System.out.println("orders");
        for (Order order : orders) {
            System.out.println(order);
        }
        orders = new PageImpl<>(filteredOrders, orders.getPageable(), orders.getTotalElements());

    return orders.map(o -> {
        Transaction transaction = o.getTransactions().getFirst();
        UUID organizationId = o.getOffer().getPublisherInfo().type().equals(PublisherType.PARTNER_SB) ? this.subEntityService.getEntityById(id).getOrganizationEntity().getId() : id;
        Commission commission = this.commissionService.getCommissionByPartnerId(id);
        System.out.println(commission);
        BigDecimal cashAmount = transaction.getType().equals(TransactionType.CASH) ? transaction.getPrice().amount() : BigDecimal.ZERO;
        BigDecimal cardAmount = transaction.getType().equals(TransactionType.CASH) ? BigDecimal.ZERO : transaction.getPrice().amount();
        BigDecimal cashCommission = transaction.getType().equals(TransactionType.CASH)
                ? BigDecimal.valueOf(commission.getCash()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount())
                : BigDecimal.ZERO;
        BigDecimal cardCommission = transaction.getType().equals(TransactionType.CASH)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(commission.getCard()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount());
        BigDecimal amount = transaction.getPrice().amount();
        System.out.println(commission.getCard());

        return new MonthlyOperationsDto(null, o.getId(), amount, o.getQuantity(), cashAmount, cashCommission, cardAmount, cardCommission);
        });
    }
}

