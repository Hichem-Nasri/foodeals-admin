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
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.offer.domain.enums.PublisherType;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.enums.OrderStatus;
import net.foodeals.order.domain.enums.TransactionStatus;
import net.foodeals.order.domain.enums.TransactionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.response.*;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.PartnerI;
import net.foodeals.payment.domain.entities.PaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import net.foodeals.user.domain.entities.User;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        return this.partnerCommissionsRepository.findCommissionsByDateAndPartnerInfoTypeNot(year, month, PartnerType.DELIVERY_PARTNER);
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
    public SubscriptionPaymentDto toSubscriptionPaymentDto(Subscription subscription) {
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
//        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder().id(UUID.randomUUID()).name(partnerName)
//                .avatarPath(avatarPath)
//                .build();
//        subscriptionPaymentDto.setPartnerType(subscription.getPartnerType());
//        subscriptionPaymentDto.setPartnerInfoDto(partnerInfoDto);
        return null;
    }
    @Override
    @Transactional
            public List<String> getAvailableMonthsByPartner(UUID partnerId) {
            return partnerCommissionsRepository.findDistinctMonthsByPartner(partnerId);
        }
    @Override
    @Transactional
        public List<String> getAvailableMonthsByOrganization(UUID organizationId) {
            return partnerCommissionsRepository.findDistinctMonthsByOrganization(organizationId);
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
    public void receiveSubscription(ReceiveDto receiveDto) {
        Deadlines deadlines = this.deadlinesService.findById(receiveDto.id());

        if (deadlines == null) {
            throw  new ResourceNotFoundException("deadline not found with id " + receiveDto.id().toString());
        }

        if (deadlines.getSubscription().getPartner().type().equals(PartnerType.PARTNER_SB)) {
            Set<Deadlines> childs = deadlines.getSubEntityDeadlines().stream().map(c -> {
                c.setStatus(DeadlineStatus.CONFIRMED_BY_FOODEALS);
                Subscription subscription = c.getSubscription();

                subscription.setSubscriptionStatus(SubscriptionStatus.VALID);
                subscription.getDeadlines().forEach(d -> {
                    if (c.getId() != d.getId() && !d.getStatus().equals(DeadlineStatus.CONFIRMED_BY_FOODEALS)) {
                        subscription.setSubscriptionStatus(SubscriptionStatus.IN_PROGRESS);
                    }
                });
                this.subscriptionService.save(subscription);
                return  c;
            }).collect(Collectors.toSet());
            this.deadlinesService.saveAll(new ArrayList<>(childs));
        } else if (deadlines.getSubscription().getPartner().type().equals(PartnerType.SUB_ENTITY)) {
            Deadlines parent = deadlines.getParentPartner();
            parent.setStatus(DeadlineStatus.CONFIRMED_BY_FOODEALS);
            parent.getSubEntityDeadlines().forEach(c -> {
                if (c.getId() != deadlines.getId() && !c.getStatus().equals(DeadlineStatus.CONFIRMED_BY_FOODEALS)) {
                    parent.setStatus(DeadlineStatus.IN_VALID);
                }
            });
            Subscription subscription = parent.getSubscription();

            subscription.setSubscriptionStatus(SubscriptionStatus.VALID);
            subscription.getDeadlines().forEach(d -> {
                if (parent.getId() != d.getId() && !d.getStatus().equals(DeadlineStatus.CONFIRMED_BY_FOODEALS)) {
                    subscription.setSubscriptionStatus(SubscriptionStatus.IN_PROGRESS);
                }
            });
            this.subscriptionService.save(subscription);
            this.deadlinesService.save(parent);
        }
        deadlines.setStatus(DeadlineStatus.CONFIRMED_BY_FOODEALS);

        Subscription subscription = deadlines.getSubscription();

        subscription.setSubscriptionStatus(SubscriptionStatus.VALID);
        subscription.getDeadlines().forEach(d -> {
            if (deadlines.getId() != d.getId() && !d.getStatus().equals(DeadlineStatus.CONFIRMED_BY_FOODEALS)) {
                subscription.setSubscriptionStatus(SubscriptionStatus.IN_PROGRESS);
            }
        });
        this.subscriptionService.save(subscription);
        this.deadlinesService.save(deadlines);
    }


    @Override
    public PaymentFormData getFormData(UUID id, PaymentType type) {
        return switch (type) {
            case PaymentType.COMMISSION -> this.getCommissionFormData(id);
            case PaymentType.SUBSCRIPTION -> this.getSubscriptionFormData(id);
            default -> null;
        };
    }

    @Override
    @Transactional
    public PaymentFormData getCommissionFormData(UUID id) {
        PartnerCommissions partnerCommission = this.partnerCommissionsRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("commission not found with id " + id.toString()));
        PartnerInfoDto partnerInfoDto = null;
        if (partnerCommission.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
            SubEntity subEntity = this.subEntityService.getEntityById(partnerCommission.getPartnerInfo().id());
            partnerInfoDto = new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
        } else {
            OrganizationEntity partner = this.organizationEntityService.findById(partnerCommission.getPartnerInfo().id());
            partnerInfoDto = new PartnerInfoDto(partner.getId(), partner.getName(), partner.getAvatarPath());
        }
        User emitter = partnerCommission.getEmitter();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/y");
        PaymentMethod paymentMethod = partnerCommission.getPaymentMethod();
        String date = formatter.format(paymentMethod.getOperationDate());
        String document = paymentMethod.getDocumentPath();
        Price price = paymentMethod.getPrice();
        return new PaymentFormData(paymentMethod.getType(), partnerInfoDto, emitter.getName(), price, document , date);
    }

    @Override
    public PaymentFormData getSubscriptionFormData(UUID id) {
        return null;
    }

    @Override
    @Transactional
    public MonthlyOperationsDto monthlyOperations(UUID id, int year, int month, Pageable page, PartnerType type) {
        return switch (type) {
            case PartnerType.PARTNER_SB, PartnerType.NORMAL_PARTNER, PartnerType.SUB_ENTITY -> partner(id, year, month, page);
//            case PartnerType.DELIVERY_PARTNER -> deliveryPartner(id, year, month, page);
            default -> null;
        };
    }

//    private MonthlyOperationsDto deliveryPartner(UUID id, int year, int month, Pageable page) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month - 1, 1);
//        Date date = calendar.getTime();
//        PartnerCommissions commissions = this.partnerCommissionsRepository.findCommissionsByDateAndPartnerInfoId(year, month, id);
//        Page<Order> orders = this.orderService.findOrdersByOrganizationAndDeliveryStatusAndCriteria(id, DeliveryStatus.DELIVERED,date, OrderStatus.COMPLETED, TransactionStatus.COMPLETED, page);
//        PaymentStatistics statistics = this.getPaymentStatistics(new ArrayList<>(List.of(commissions)));
//        PartnerInfoDto  partnerInfoDto;
//        if (commissions.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
//            SubEntity subEntity = this.subEntityService.getEntityById(commissions.getPartnerInfo().id());
//            partnerInfoDto = new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
//        } else {
//            OrganizationEntity partner = this.organizationEntityService.findById(commissions.getPartnerInfo().id());
//            partnerInfoDto = new PartnerInfoDto(partner.getId(), partner.getName(), partner.getAvatarPath());
//        }
//
//        Page<OperationsDto> operationsDtos = orders.map(o -> {
//            Transaction transaction = o.getTransaction();
//            UUID organizationId = o.getOffer().getPublisherInfo().type().equals(PublisherType.PARTNER_SB) ? this.subEntityService.getEntityById(id).getOrganizationEntity().getId() : o.getOffer().getPublisherInfo().id();
//            Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
//            Price cashAmount = transaction.getType().equals(TransactionType.CASH) ? transaction.getPrice() : Price.ZERO(Currency.getInstance("MAD"));
//            Price cardAmount = transaction.getType().equals(TransactionType.CASH) ? Price.ZERO(Currency.getInstance("MAD")) : transaction.getPrice();
//            Price cashCommission = transaction.getType().equals(TransactionType.CASH)
//                    ? new Price(BigDecimal.valueOf(commission.getCash()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount()), Currency.getInstance("MAD"))
//                    : Price.ZERO(Currency.getInstance("MAD"));
//            Price cardCommission = transaction.getType().equals(TransactionType.CASH)
//                    ? Price.ZERO(Currency.getInstance("MAD"))
//                    : new Price(BigDecimal.valueOf(commission.getCard()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount()), Currency.getInstance("MAD"));
//            Price amount = transaction.getPrice();
//            ProductInfo productInfo = new ProductInfo(o.getOffer().getTitle(), o.getOffer().getImagePath());
//            return new OperationsDto(productInfo, o.getId(), amount, o.getQuantity(), cashAmount, cashCommission, cardAmount, cardCommission);
//        });
//        CommissionPaymentDto dto = this.modelMapper.map(commissions, CommissionPaymentDto.class);
//        PaymentsDetailsDto detailsDto = new PaymentsDetailsDto(dto.getId(), dto.isPayable(), dto.getPaymentStatus(), dto.getToPay().amount().compareTo(dto.getToReceive().amount()) > 0 ? PaymentDirection.FOODEALS_TO_PARTENER : PaymentDirection.PARTNER_TO_FOODEALS);
//        return new MonthlyOperationsDto(partnerInfoDto, statistics, operationsDtos, detailsDto);
//    }

    private MonthlyOperationsDto partner(UUID id, int year, int month, Pageable page) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        Date date = calendar.getTime();
        PartnerCommissions commissions = this.partnerCommissionsRepository.findCommissionsByDateAndPartnerInfoId(year, month, id);
        Page<Order> orders = this.orderService.findByOfferPublisherInfoIdAndDateAndStatus(id, date, OrderStatus.COMPLETED, TransactionStatus.COMPLETED, page);
        PaymentStatistics statistics = this.getPaymentStatistics(new ArrayList<>(List.of(commissions)));
        PartnerInfoDto  partnerInfoDto;
        if (commissions.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
            SubEntity subEntity = this.subEntityService.getEntityById(commissions.getPartnerInfo().id());
            partnerInfoDto = new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
        } else {
            OrganizationEntity partner = this.organizationEntityService.findById(commissions.getPartnerInfo().id());
            partnerInfoDto = new PartnerInfoDto(partner.getId(), partner.getName(), partner.getAvatarPath());
        }

        Page<OperationsDto> operationsDtos = orders.map(o -> {
            Transaction transaction = o.getTransaction();
            UUID organizationId = o.getOffer().getPublisherInfo().type().equals(PublisherType.PARTNER_SB) ? this.subEntityService.getEntityById(id).getOrganizationEntity().getId() : id;
            Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
            Price cashAmount = transaction.getType().equals(TransactionType.CASH) ? transaction.getPrice() : Price.ZERO(Currency.getInstance("MAD"));
            Price cardAmount = transaction.getType().equals(TransactionType.CASH) ? Price.ZERO(Currency.getInstance("MAD")) : transaction.getPrice();
            Price cashCommission = transaction.getType().equals(TransactionType.CASH)
                    ? new Price(BigDecimal.valueOf(commission.getCash()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount()), Currency.getInstance("MAD"))
                    : Price.ZERO(Currency.getInstance("MAD"));
            Price cardCommission = transaction.getType().equals(TransactionType.CASH)
                    ? Price.ZERO(Currency.getInstance("MAD"))
                    : new Price(BigDecimal.valueOf(commission.getCard()).divide(BigDecimal.valueOf(100)).multiply(transaction.getPrice().amount()), Currency.getInstance("MAD"));
            Price amount = transaction.getPrice();
            ProductInfo productInfo = new ProductInfo(o.getOffer().getTitle(), o.getOffer().getImagePath());
            return new OperationsDto(productInfo, o.getId(), amount, o.getQuantity(), cashAmount, cashCommission, cardAmount, cardCommission);
        });
        CommissionPaymentDto dto = this.modelMapper.map(commissions, CommissionPaymentDto.class);
        PaymentsDetailsDto detailsDto = new PaymentsDetailsDto(dto.getId(), dto.isPayable(), dto.getPaymentStatus(), dto.getToPay().amount().compareTo(dto.getToReceive().amount()) > 0 ? PaymentDirection.FOODEALS_TO_PARTENER : PaymentDirection.PARTNER_TO_FOODEALS);
        return new MonthlyOperationsDto(partnerInfoDto, statistics, operationsDtos, detailsDto);
    }

    @Override
    public List<PartnerCommissions> getCommissionPaymentsByOrganizationId(UUID id, int year, int month) {
        return this.partnerCommissionsRepository.findCommissionsByDateAndOrganizationAndPartnerInfoTypeNot(year, month, id, PartnerType.DELIVERY_PARTNER);
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
                if (partnerCommissions.getPartnerInfo().type().equals(PartnerType.DELIVERY_PARTNER)) {
                    List<Order> orderList = this.orderService.findOrdersByOrganizationAndDeliveryStatusAndCriteria(partnerCommissions.getPartner().getId(), DeliveryStatus.DELIVERED, partnerCommissions.getDate(), OrderStatus.COMPLETED, TransactionStatus.COMPLETED);
                    total.updateAndGet(current -> current.add(new BigDecimal(orderList.size() * commission.getDeliveryAmount())));
                    totalCommission.updateAndGet(current -> current.add(new BigDecimal(orderList.size() * commission.getDeliveryCommission())));
                } else {
                    List<Order> orderList = this.orderService.findByOfferPublisherInfoIdAndDateAndStatus(partnerCommissions.getPartner().getId(), partnerCommissions.getDate(), OrderStatus.COMPLETED, TransactionStatus.COMPLETED);
                    List<Transaction> transactions = orderList.stream()
                            .map(order -> order.getTransaction())
                            .collect(Collectors.toList());
                    BigDecimal paymentsWithCash = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH))
                            .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal paymentsWithCard = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD))
                            .map(transaction -> transaction.getPrice().amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Double commissionTotal = ((Double) (commission.getCard().doubleValue() / 100)) * paymentsWithCard.doubleValue() + ((Double) (commission.getCash().doubleValue() / 100)) * paymentsWithCash.doubleValue();
                    total.updateAndGet(current -> current.add(paymentsWithCash.add(paymentsWithCard)));
                    totalCommission.updateAndGet(current -> current.add(new BigDecimal(commissionTotal)));
                }
            }
        });
        return new PaymentStatistics(new Price(total.get().setScale(3, RoundingMode.HALF_UP), Currency.getInstance("MAD")), new Price(totalCommission.get().setScale(3, RoundingMode.HALF_UP),Currency.getInstance("MAD")));
    }

    @Override
    @Transactional
    public PaymentResponse receive(ReceiveDto receiveDto, PaymentType type) throws BadRequestException {
        if (type.equals(PaymentType.COMMISSION)) {
            this.receiveCommission(receiveDto);
        } else {
            this.receiveSubscription(receiveDto);
        }
        return new PaymentResponse("payment validated successfully");
    }

    @Override
    @Transactional
    public void receiveCommission(ReceiveDto receiveDto) throws BadRequestException {
        PartnerCommissions commission = this.partnerCommissionsRepository.findById(receiveDto.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id " + receiveDto.id().toString()));
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

    @Override
    @Transactional
    public SubscriptionPaymentDto getSubscriptionResponse(int year, Pageable page, UUID id) {
        // 1. Fetch subscriptions for the given year
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        // 2. Get all subscriptions for the year
        List<Subscription> subscriptions = subscriptionService.findByStartDateBetweenAndSubscriptionStatusNot(startOfYear, endOfYear, SubscriptionStatus.NOT_STARTED, id);

        // 3. Calculate total statistics
        Price totalSubscriptions = calculateTotalSubscriptions(subscriptions);
        Price totalDeadlines = calculateTotalDeadlines(subscriptions);
        SubscriptionStatistics statistics = new SubscriptionStatistics(totalSubscriptions, totalDeadlines);

        // 4. Group subscriptions by partner
        Map<UUID, List<Subscription>> subscriptionsByPartner = subscriptions.stream()
                .collect(Collectors.groupingBy(s -> s.getPartner().id()));

        // 5. Create SubscriptionsListDto for each partner
        List<SubscriptionsListDto> subscriptionsList = subscriptionsByPartner.entrySet().stream()
                .map(entry -> createSubscriptionsListDto(entry.getKey(), entry.getValue(), year))
                .collect(Collectors.toList());

        // 6. Create final page
        Page<SubscriptionsListDto> subscriptionsPage = new PageImpl<>(
                subscriptionsList,
                page,
                subscriptions.size()
        );

        return new SubscriptionPaymentDto(statistics, subscriptionsPage);
    }

    @Override
    @Transactional
    public List<String> getAvailableMonths() {
            return partnerCommissionsRepository.findDistinctMonths();
        }

        @Override
        @Transactional
        public Page<PartnerInfoDto> searchPartnersByNameCommission(String name, List<PartnerType> types, Pageable pageable, UUID id) {
            if (id != null) {
                // If an ID is provided, fetch the specific partner
                Optional<PartnerCommissions> partnerOptional = partnerCommissionsRepository.findCommissionsByPartnerInfoId(id);
                if (partnerOptional.isPresent()) {
                    PartnerCommissions partner = partnerOptional.get();
                    PartnerInfoDto partnerInfoDto = mapCommissionToPartnerInfoDto(partner);
                    return new PageImpl<>(Collections.singletonList(partnerInfoDto), pageable, 1);
                } else {
                    return new PageImpl<>(Collections.emptyList(), pageable, 0); // No partner found
                }
            } else {
                // If no ID is provided, perform the search by name and types
                return partnerCommissionsRepository.findByPartnerNameAndTypeIn(name, types, pageable).map(this::mapCommissionToPartnerInfoDto);
            }
        }

    @Override
    @Transactional
    public Page<PartnerInfoDto> searchPartnersByNameSubscription(String name, List<PartnerType> types, Pageable pageable, UUID id) {
        if (id != null) {
            // If an ID is provided, fetch the specific partner
            Optional<Subscription> partnerOptional = subscriptionService.findSubscriptionByPartnerInfoId(id, types);
            if (partnerOptional.isPresent()) {
                Subscription partner = partnerOptional.get();
                PartnerInfoDto partnerInfoDto = mapSubscriptionToPartnerInfoDto(partner);
                return new PageImpl<>(Collections.singletonList(partnerInfoDto), pageable, 1);
            } else {
                return new PageImpl<>(Collections.emptyList(), pageable, 0); // No partner found
            }
        } else {
            // If no ID is provided, perform the search by name and types
            return subscriptionService.findByPartnerNameAndTypeIn(name, types, pageable).map(this::mapSubscriptionToPartnerInfoDto);
        }
    }

        private PartnerInfoDto mapCommissionToPartnerInfoDto(PartnerCommissions partner) {
            if (partner.getPartnerInfo().type().equals(PartnerType.SUB_ENTITY)) {
                SubEntity subEntity = this.subEntityService.getEntityById(partner.getPartnerInfo().id());
                return new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
            } else {
                OrganizationEntity organizationEntity = this.organizationEntityService.findById(partner.getPartnerInfo().id());
                return new PartnerInfoDto(organizationEntity.getId(), organizationEntity.getName(), organizationEntity.getAvatarPath());
            }
        }

    private PartnerInfoDto mapSubscriptionToPartnerInfoDto(Subscription partner) {
        if (partner.getPartner().type().equals(PartnerType.SUB_ENTITY)) {
            SubEntity subEntity = this.subEntityService.getEntityById(partner.getPartner().id());
            return new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
        } else {
            OrganizationEntity organizationEntity = this.organizationEntityService.findById(partner.getPartner().id());
            return new PartnerInfoDto(organizationEntity.getId(), organizationEntity.getName(), organizationEntity.getAvatarPath());
        }
    }



    @Override
    @Transactional
    public List<Integer> getAvailableYears() {
        return subscriptionService.findDistinctYears();
    }

    @Override
    @Transactional
    public SubscriptionDetails getSubscriptionDetails(int year, UUID id) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Subscription> subscriptions = subscriptionService.findByStartDateBetweenAndSubscriptionStatusNotAndId(startOfYear, endOfYear, SubscriptionStatus.NOT_STARTED, id);

        Subscription firstSubscription = subscriptions.get(0);
        PartnerI partner = !firstSubscription.getPartner().type().equals(PartnerType.SUB_ENTITY) ? this.organizationEntityService.findById(firstSubscription.getPartner().id()) : this.subEntityService.getEntityById(firstSubscription.getPartner().id());
        subscriptions.forEach(s -> {
            s.setPartnerI(partner);
        });
        List<SubscriptionsDto> subscriptionsDto =  subscriptions.stream()
                .map(s -> {
                    return this.mapToSubscriptionsDto(s);
                })
                .collect(Collectors.toList());
        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(partner.getId(), partner.getName(), partner.getAvatarPath());

        return new SubscriptionDetails(partnerInfoDto, subscriptionsDto);
    }

    @Override
    @Transactional
    public DeliveryPaymentResponse getDeliveryPayments(int year, Pageable page, UUID id) {
        List<PartnerCommissions> commissions = this.partnerCommissionsRepository.findCommissionsByDateAndOrganization(year, id);
        int start = (int) page.getOffset();
        int end = Math.min((start + page.getPageSize()), commissions.size());
        List<PartnerCommissions> pageContent = commissions.subList(start, end);
        Page<PartnerCommissions> commissionsPage = new PageImpl<>(pageContent, page, pageContent.size());
        Page<DeliveryPaymentDto> commissionsDtos = this.convertToDeliveryCommission(commissionsPage);
//        PaymentStatistics statistics = this.getPaymentStatistics(commissions);
        return new DeliveryPaymentResponse(null, commissionsDtos);
        }

    @Override
    @Transactional
    public Page<DeliveryPaymentDto> convertToDeliveryCommission(Page<PartnerCommissions> commissionsPage) {
        Commission commission = this.commissionService.getCommissionByPartnerId(commissionsPage.getContent().getFirst().getPartnerInfo().id());
        Currency mad = Currency.getInstance("MAD");
        SimpleDateFormat formatter = new SimpleDateFormat("M/yyyy");
        return commissionsPage.map(c -> {
            List<Order> orderList = this.orderService.findOrdersByOrganizationAndDeliveryStatusAndCriteria(c.getPartnerInfo().id(), DeliveryStatus.DELIVERED, c.getDate(), OrderStatus.COMPLETED, TransactionStatus.COMPLETED);
            List<Transaction> transactions = orderList.stream()
                    .map(order -> order.getTransaction())
                    .collect(Collectors.toList());
            // cash 200 20
            // card 10
            // 210 - 60
            Float paymentsWithCash = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH))
                    .collect(Collectors.toList()).size() * commission.getDeliveryAmount();
            Float paymentsWithCard = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD))
                    .collect(Collectors.toList()).size() * commission.getDeliveryAmount();
            Double commissionTotal = (transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CARD))
                    .collect(Collectors.toList()).size() + transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CASH))
                    .collect(Collectors.toList()).size()) * Double.valueOf(commission.getDeliveryCommission());
            Double difference = (paymentsWithCard.doubleValue() - commissionTotal);
            Double toPay = difference < 0 ? 0 : difference;
            Double toReceive = difference < 0 ? Math.abs(difference) : 0;

            return new DeliveryPaymentDto(formatter.format(c.getDate()), new Price(new BigDecimal(commission.getDeliveryAmount()), mad), new Price(new BigDecimal(commission.getDeliveryCommission()), mad), Long.valueOf(orderList.size()), new Price(new BigDecimal(orderList.size() * commission.getDeliveryCommission()), mad), new Price(new BigDecimal(toPay), mad), new Price(new BigDecimal(toReceive), mad), c.getPaymentStatus());
        });
    }

    @Transactional
    private Price calculateTotalSubscriptions(List<Subscription> subscriptions) {
        List<Subscription> filteredSubscriptions = subscriptions.stream()
                .filter(subscription ->
                        subscription.getPartner().type().equals(PartnerType.NORMAL_PARTNER)
                                || (subscription.getPartner().type().equals(PartnerType.SUB_ENTITY)))
                .collect(Collectors.toList());

// Then check if the filtered list is empty and calculate accordingly

        return filteredSubscriptions.isEmpty()
                ? Price.ZERO(Currency.getInstance("MAD"))
                : filteredSubscriptions.stream()
                .map(Subscription::getAmount)
                .reduce(Price.ZERO(Currency.getInstance("MAD")), Price::add);
    }

    @Transactional
    private Price calculateTotalSubscriptionsOfPrincipal(UUID id, int year) {

        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Subscription> subscriptions =  subscriptionService.findByStartDateBetweenAndSubscriptionStatusNot(startOfYear, endOfYear, SubscriptionStatus.NOT_STARTED, id);

        List<Subscription> filter =  subscriptions.stream().filter(s -> s.getPartner().type().equals(PartnerType.SUB_ENTITY)).collect(Collectors.toList());

        return filter.isEmpty()
                ? Price.ZERO(Currency.getInstance("MAD"))
                : filter.stream()
                .map(Subscription::getAmount)
                .reduce(Price.ZERO(Currency.getInstance("MAD")), Price::add);
    }

    // case 1 -<  some sub not has all subscriptions ->
    @Transactional
    private Price calculateTotalDeadlines(List<Subscription> subscriptions) {
// First, get the filtered subscriptions
        List<Subscription> filteredSubscriptions = subscriptions.stream()
                .filter(subscription ->
                        subscription.getPartner().type().equals(PartnerType.NORMAL_PARTNER)
                                || (subscription.getPartner().type().equals(PartnerType.SUB_ENTITY)))
                .collect(Collectors.toList());

// Then check if the filtered list is empty and calculate accordingly
        Price totalAmount;
        if (filteredSubscriptions.isEmpty()) {
            totalAmount = Price.ZERO(Currency.getInstance("MAD"));
        } else {
            totalAmount = filteredSubscriptions.stream()
                    .map(s -> s.getDeadlines().getFirst().getAmount())
                    .reduce(Price.ZERO(Currency.getInstance("MAD")), Price::add);
        }

        return totalAmount;
    }

    @Transactional
    private SubscriptionsListDto createSubscriptionsListDto(UUID partnerId, List<Subscription> partnerSubscriptions, int year) {
        // Get partner information from the first subscription
        Subscription firstSubscription = partnerSubscriptions.get(0);
        PartnerI partner = !firstSubscription.getPartner().type().equals(PartnerType.SUB_ENTITY) ? this.organizationEntityService.findById(firstSubscription.getPartner().id()) : this.subEntityService.getEntityById(firstSubscription.getPartner().id());
        partnerSubscriptions.forEach(s -> {
            s.setPartnerI(partner);
        });

        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(
                partner.getId(),
                partner.getName(),
                partner.getAvatarPath()
        );

        // Calculate total amount for this partner
        Price partnerTotal = partner.getPartnerType().equals(PartnerType.PARTNER_SB) ? calculateTotalSubscriptionsOfPrincipal(partnerId, year) :  calculateTotalSubscriptions(partnerSubscriptions);

        return new SubscriptionsListDto(
                partner.getPartnerType().equals(PartnerType.SUB_ENTITY) ? ((SubEntity) partner).getContract().getId() : ((OrganizationEntity) partner).getContract().getId(),
                partnerInfoDto,
                firstSubscription.getPartner().type(),
                partnerTotal,
                partnerSubscriptions.stream()
                        .flatMap(subscription -> subscription.getSolutions().stream())
                        .map(Solution::getName)  // assuming Solution class has a getName() method
                        .distinct()  // to remove duplicates if any
                        .collect(Collectors.toList()),
                partner.getPartnerType().equals(PartnerType.NORMAL_PARTNER) ||
                        (partner.subscriptionPayedBySubEntities()  && partner.getPartnerType().equals(PartnerType.SUB_ENTITY))
                ||  (!partner.subscriptionPayedBySubEntities()  && partner.getPartnerType().equals(PartnerType.PARTNER_SB))
        );
    }

    // create ->

    @Transactional
    private SubscriptionsDto mapToSubscriptionsDto(Subscription subscription) {
        List<DeadlinesDto> deadlinesDto = subscription.getDeadlines().stream()
                .sorted(Comparator.comparing(Deadlines::getCreatedAt))  // Sort by createdAt
                .map(deadline -> {
                    LocalDate dueDate = deadline.getDueDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("fr", "FR"));
                    String formattedDate = dueDate.format(formatter);

                    boolean isPaymentEligible = subscription.getPartner().type().equals(PartnerType.NORMAL_PARTNER)
                            || (subscription.getPartner().type().equals(PartnerType.SUB_ENTITY) && ((PartnerI)subscription.getPartnerI()).subscriptionPayedBySubEntities())
                            || (subscription.getPartner().type().equals(PartnerType.PARTNER_SB) && ((PartnerI)subscription.getPartnerI()).subscriptionPayedBySubEntities() == false);

                    return new DeadlinesDto(
                            deadline.getId(),
                            formattedDate,
                            deadline.getStatus(),
                            !(subscription.getPartnerI().getPartnerType().equals(PartnerType.PARTNER_SB))
                                    ? deadline.getAmount()
                                    : deadline.getSubEntityDeadlines().stream()
                                    .map(d -> d.getAmount())
                                    .reduce(Price.ZERO(Currency.getInstance("MAD")), Price::add),
                            isPaymentEligible
                    );
                })
                .collect(Collectors.toList());

        return new SubscriptionsDto(
                subscription.getId(),
                !(subscription.getPartnerI().getPartnerType().equals(PartnerType.PARTNER_SB))
                        ? subscription.getAmount()
                        : subscription.getSubEntities().stream()
                        .map(d -> d.getAmount())
                        .reduce(Price.ZERO(Currency.getInstance("MAD")), Price::add),
                subscription.getSolutions().stream().map(s -> s.getName()).toList(),
                deadlinesDto
        );
    }

}
