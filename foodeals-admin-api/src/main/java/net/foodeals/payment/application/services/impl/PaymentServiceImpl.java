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
import net.foodeals.common.valueOjects.Price;
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
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.response.*;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    public List<PartnerCommissions> getCommissionPayments(int year, int month) {
        return this.partnerCommissionsRepository.findCommissionsByDate(year, month);
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

    @Transactional
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
        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().id(UUID.randomUUID()).name(partnerName)
                .avatarPath(avatarPath)
                .build();
        subscriptionPaymentDto.setPartnerType(subscription.getPartnerType());
        subscriptionPaymentDto.setPartnerInfoDto(partnerInfoDto);
        return subscriptionPaymentDto;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, MultipartFile document, PaymentType type) throws BadRequestException {
        PaymentProcessor processor = processors.get(request.paymentMethod().toLowerCase());
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + request.paymentMethod());
        }
        return processor.process(request, document, type);
    }

    // convert to dto
       // case of partner with subentities.

    @Override
    @Transactional
    public Page<CommissionPaymentDto> convertCommissionToDto(Page<PartnerCommissions> payments) {
        Page<CommissionPaymentDto> paymentDtos = payments.map(payment -> {
            if (payment.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
                SubEntity subEntity = this.subEntityService.getEntityById(payment.getPartnerInfo().id());
                payment.setPartner(subEntity);
            } else {
                OrganizationEntity organizationEntity = this.organizationEntityService.findById(payment.getPartnerInfo().id());
                payment.setPartner(organizationEntity);
            }
            CommissionPaymentDto dto = null;
            if (payment.getPartnerInfo().type().equals(PartnerType.PARTNER_SB)) {
                List<CommissionPaymentDto> childs = payment.getSubEntityCommissions().stream().map((PartnerCommissions p) -> {
                    if (p.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
                        SubEntity subEntity = this.subEntityService.getEntityById(p.getPartnerInfo().id());
                        p.setPartner(subEntity);
                    } else {
                        OrganizationEntity organizationEntity = this.organizationEntityService.findById(p.getPartnerInfo().id());
                        p.setPartner(organizationEntity);
                    }
                    return  this.modelMapper.map(p, CommissionPaymentDto.class);
                }).collect(Collectors.toList());

                dto = new CommissionPaymentDto();
                Currency defaultCurrency = Currency.getInstance("MAD");
                Price totalCommission = !childs.isEmpty()
                        ? childs.stream()
                        .map(p -> p.getFoodealsCommission())
                        .reduce(Price.ZERO(defaultCurrency), (price1, price2) -> Price.add((Price)price1, (Price)price2))
                        : Price.ZERO(defaultCurrency);

                Price toPay = !childs.isEmpty()
                        ? childs.stream()
                        .map(p -> p.getToPay())
                        .reduce(Price.ZERO(defaultCurrency), (price1, price2) -> Price.add((Price)price1, (Price)price2))
                        : Price.ZERO(defaultCurrency);

                Price toReceive = !childs.isEmpty()
                        ? childs.stream()
                        .map(p -> p.getToReceive())
                        .reduce(Price.ZERO(defaultCurrency), (price1, price2) -> Price.add((Price)price1, (Price)price2))
                        : Price.ZERO(defaultCurrency);

                Price totalAmount = !childs.isEmpty()
                        ? childs.stream()
                        .map(p -> p.getTotalAmount())
                        .reduce(Price.ZERO(defaultCurrency), (price1, price2) -> Price.add((Price)price1, (Price)price2))
                        : Price.ZERO(defaultCurrency);
                dto.setId(payment.getId());
                dto.setEntityId(payment.getPartnerInfo().id());
                PartnerInfoDto partnerInfoDto = new PartnerInfoDto(payment.getPartnerInfo().id(), payment.getPartner().getName(), payment.getPartner().getAvatarPath());
                dto.setPartnerInfoDto(partnerInfoDto);
                dto.setPartnerType(payment.getPartnerInfo().type());
                dto.setOrganizationId(payment.getPartnerInfo().id());
                SimpleDateFormat formatter = new SimpleDateFormat("M/yyyy");
                dto.setDate(formatter.format(payment.getDate()));
                dto.setTotalAmount(totalAmount);
                dto.setFoodealsCommission(totalCommission);
                dto.setToPay(toPay);
                dto.setToReceive(toReceive);
                dto.setPaymentStatus(payment.getPaymentStatus());
                dto.setPayable(payment.getPaymentResponsibility().equals(PaymentResponsibility.PAYED_BY_PARTNER));
                dto.setCommissionPayedBySubEntities(payment.getPaymentResponsibility().equals(PaymentResponsibility.PAYED_BY_SUB_ENTITIES));
            } else {
                dto = this.modelMapper.map(payment, CommissionPaymentDto.class);
            }
            return dto;
        });
        return paymentDtos;
    }

    @Override
    @Transactional
    public void receiveSubscription(MultipartFile document, ReceiveDto receiveDto) {
        Deadlines deadlines = this.deadlinesService.findById(receiveDto.id());

        if (deadlines == null) {
            throw  new ResourceNotFoundException("deadline not found with id " + receiveDto.id().toString());
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
        calendar.set(year, month - 1, 1);
        Date date = calendar.getTime();
        Page<Order> orders = this.orderService.findByOfferPublisherInfoIdAndDate(id, date, page);
        List<Order> filteredOrders  = orders.getContent().stream()
                .filter(o -> !o.getTransactions().isEmpty() &&
                        o.getTransactions().get(0).getStatus().equals(TransactionStatus.COMPLETED))
                .collect(Collectors.toList());
        orders = new PageImpl<>(filteredOrders, orders.getPageable(), orders.getTotalElements());

    return orders.map(o -> {
        Transaction transaction = o.getTransactions().getFirst();
        UUID organizationId = o.getOffer().getPublisherInfo().type().equals(PublisherType.PARTNER_SB) ? this.subEntityService.getEntityById(id).getOrganizationEntity().getId() : id;
        Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
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

    @Override
    public List<PartnerCommissions> getCommissionPaymentsByOrganizationId(UUID id, int year, int month) {
        return this.partnerCommissionsRepository.findCommissionsByDateAndOrganization(year, month, id);
    }

    @Override
    @Transactional
    public CommissionDto getCommissionResponse(List<PartnerCommissions> commissions, Pageable page) {
        int start = (int) page.getOffset();
        int end = Math.min((start + page.getPageSize()), commissions.size());
        List<PartnerCommissions> pageContent = commissions.subList(start, end);
        Page<PartnerCommissions> commissionsPage = new PageImpl<>(pageContent, page, pageContent.size());
        Page<CommissionPaymentDto> commissionsDtos = this.convertCommissionToDto(commissionsPage);
        PaymentStatistics statistics = this.getPaymentStatistics(commissions);
        return new CommissionDto(statistics, commissionsDtos);
    }

    @Override
    @Transactional
    public PaymentStatistics getPaymentStatistics(List<PartnerCommissions> commissions) {
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalCommission = new AtomicReference<>(BigDecimal.ZERO);

        commissions.stream().forEach(partnerCommissions -> {
            if (!partnerCommissions.getPartnerInfo().type().equals(PartnerType.PARTNER_SB)) {
                if (partnerCommissions.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
                    partnerCommissions.setPartner(this.subEntityService.getEntityById(partnerCommissions.getPartnerInfo().id()));
                } else {
                    partnerCommissions.setPartner(this.organizationEntityService.findById(partnerCommissions.getPartnerInfo().id()));
                }
                UUID organizationId = !partnerCommissions.getPartner().getPartnerType().equals(PartnerType.SUB_ENTITY) ? partnerCommissions.getPartner().getId() : ((SubEntity) partnerCommissions.getPartner()).getOrganizationEntity().getId();
                Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
                List<Order> orderList = this.orderService.findByOfferPublisherInfoIdAndDate(partnerCommissions.getPartner().getId(), partnerCommissions.getDate());
                List<Transaction> transactions = orderList.stream()
                        .flatMap(order -> order.getTransactions().stream())
                        .collect(Collectors.toList());
                BigDecimal paymentsWithCash = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                        .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal paymentsWithCard = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD) && transaction.getStatus().equals(TransactionStatus.COMPLETED))
                        .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                Double commissionTotal = ((Double) (commission.getCard().doubleValue() / 100)) * paymentsWithCard.doubleValue() + ((Double) (commission.getCash().doubleValue() / 100)) * paymentsWithCash.doubleValue();
                total.updateAndGet(current -> current.add(paymentsWithCash.add(paymentsWithCard)));
                totalCommission.updateAndGet(current -> current.add(new BigDecimal(commissionTotal)));
            }
        });
        return new PaymentStatistics(new Price(total.get().setScale(3, RoundingMode.HALF_UP), Currency.getInstance("MAD")), new Price(totalCommission.get().setScale(3, RoundingMode.HALF_UP),Currency.getInstance("MAD")));
    }

    @Override
    @Transactional
    public PaymentResponse receive(MultipartFile document, ReceiveDto receiveDto, PaymentType type) throws BadRequestException {
        if (type.equals(PaymentType.COMMISSION)) {
            this.receiveCommission(document, receiveDto);
        } else {
            this.receiveSubscription(document, receiveDto);
        }
        return new PaymentResponse("payment validated successfully");
    }

    @Override
    @Transactional
    public void receiveCommission(MultipartFile document, ReceiveDto receiveDto) throws BadRequestException {
        if (document.isEmpty()) {
            throw  new BadRequestException("bad document");
        }
        // logic to upload doc
        PartnerCommissions commission = this.partnerCommissionsRepository.findById(receiveDto.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id " + receiveDto.id().toString()));
        commission.setConfirmedAt(receiveDto.date());
        commission.setEmitterName(receiveDto.emitter());
        if (commission.getPartnerInfo().type().equals(PartnerType.PARTNER_SB)) {
            Set<PartnerCommissions> childs = commission.getSubEntityCommissions().stream().map(c -> {
                c.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
                return  c;
            }).collect(Collectors.toSet());
            this.partnerCommissionsRepository.saveAll(childs);
        } else if (commission.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
            PartnerCommissions parent = commission.getParentPartner();
            parent.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
            parent.getSubEntityCommissions().forEach(c -> {
                if (c.getId() != commission.getId() && !c.getPaymentStatus().equals(PaymentStatus.VALIDATED_BY_BOTH)) {
                    parent.setPaymentStatus(PaymentStatus.IN_VALID);
                }
            });
            this.partnerCommissionsRepository.save(parent);
        }
        commission.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
        this.partnerCommissionsRepository.save(commission);
    }
}

