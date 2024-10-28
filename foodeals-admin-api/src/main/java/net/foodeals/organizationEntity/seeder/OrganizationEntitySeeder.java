//package net.foodeals.organizationEntity.seeder;
//
//import jakarta.transaction.Transactional;
//import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;//package net.foodeals.organizationEntity.seeder;
//
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import net.foodeals.common.annotations.Seeder;
//import net.foodeals.common.valueOjects.Price;
//import net.foodeals.location.application.services.AddressService;
//import net.foodeals.offer.application.services.DonationService;
//import net.foodeals.offer.application.services.OfferService;
//import net.foodeals.offer.domain.entities.*;
//import net.foodeals.offer.domain.enums.DonationReceiverType;
//import net.foodeals.offer.domain.enums.DonorType;
//import net.foodeals.offer.domain.repositories.OfferRepository;
//import net.foodeals.order.domain.entities.Order;
//import net.foodeals.order.domain.entities.Transaction;
//import net.foodeals.order.domain.enums.TransactionStatus;
//import net.foodeals.order.domain.enums.TransactionType;
//import net.foodeals.order.domain.repositories.OrderRepository;
//import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
//import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
//import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
//import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
//import net.foodeals.organizationEntity.application.services.SolutionService;
//import net.foodeals.organizationEntity.domain.entities.Contact;
//import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
//import net.foodeals.organizationEntity.domain.entities.SubEntity;
//import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
//import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
//import net.foodeals.organizationEntity.domain.repositories.ContactRepository;
//import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
//import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
//import net.foodeals.payment.domain.entities.Enum.PartnerType;
//import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
//import net.foodeals.payment.domain.entities.PartnerCommissions;
//import net.foodeals.payment.domain.entities.PartnerInfo;
//import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
//import net.foodeals.user.application.dtos.requests.UserRequest;
//import net.foodeals.user.application.dtos.responses.UserResponse;
//import net.foodeals.user.application.services.UserService;
//import net.foodeals.user.domain.entities.User;
//import net.foodeals.user.domain.valueObjects.Name;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Seeder
//@AllArgsConstructor
//public class OrganizationEntitySeeder {
//
//    private final OrganizationEntityService organizationEntityService;
//    private final SubEntityRepository subEntityRepository;
//    private final DonationService donationService;
//    private final AddressService addressService;
//    private final SolutionService solutionService;
//    private final OrganizationEntityRepository organizationEntityRepository;
//    private final UserService userService;
//    private final OfferRepository offerRepository;
//    private final OrderRepository orderRepository;
//    private final ContactRepository contactRepository;
//    private final PartnerCommissionsRepository pr;
////
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    public void createAssociation() {
//        // Get the first organization to set as the publisher
//        System.out.println("seed ");
//        OrganizationEntity firstOrganization = this.organizationEntityRepository.findById(UUID.fromString("795e1a63-4590-4f3a-a8ba-1684dc2460b8")).orElse(null);
//        UUID publisherId = firstOrganization.getId();
//
//        // Create Offer
//        Offer offer = new Offer();
//        offer.setPrice(new Price(BigDecimal.valueOf(100), Currency.getInstance("MAD")));
//        offer.setSalePrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
//        offer.setReduction(20);
//        offer.setPublisherInfo(new PublisherInfo(publisherId, firstOrganization.getPublisherType()));
//        offer = this.offerRepository.save(offer);
//
//        // Create Order 1
//        Order order1 = new Order();
//        order1.setPrice(new Price(BigDecimal.valueOf(50), Currency.getInstance("MAD")));
//        order1.setOffer(offer);  // Attach the order to the offer
//
//        // Create Order 2
//        Order order2 = new Order();
//        order2.setPrice(new Price(BigDecimal.valueOf(30), Currency.getInstance("MAD")));
//        order2.setOffer(offer);  // Attach the order to the offer
//
//        // Create Transaction for Order 1
//        Transaction transaction1 = new Transaction();
//        transaction1.setPaymentId("PAY123");
//        transaction1.setReference("REF123");
//        transaction1.setContext("Context1");
//        transaction1.setPrice(new Price(BigDecimal.valueOf(20), Currency.getInstance("MAD")));
//        transaction1.setStatus(TransactionStatus.COMPLETED);
//        transaction1.setType(TransactionType.CARD);
//        transaction1.setOrder(order1);  // Attach transaction to order
//
//        Transaction transaction2 = new Transaction();
//        transaction2.setPaymentId("PAY124");
//        transaction2.setReference("REF124");
//        transaction2.setContext("Context2");
//        transaction2.setPrice(new Price(BigDecimal.valueOf(15), Currency.getInstance("MAD")));
//        transaction2.setStatus(TransactionStatus.COMPLETED);
//        transaction2.setType(TransactionType.CARD);
//        transaction2.setOrder(order1);
//
//        Transaction transaction3 = new Transaction();
//        transaction3.setPaymentId("PAY125");
//        transaction3.setReference("REF125");
//        transaction3.setContext("Context3");
//        transaction3.setPrice(new Price(BigDecimal.valueOf(15), Currency.getInstance("MAD")));
//        transaction3.setStatus(TransactionStatus.COMPLETED);
//        transaction3.setType(TransactionType.CARD);
//        transaction3.setOrder(order1);
//
//        order1.setTransactions(new ArrayList<>(Arrays.asList(transaction1, transaction2, transaction3)));
//
//        // Create Transaction for Order 2
//        Transaction transaction4 = new Transaction();
//        transaction4.setPaymentId("PAY126");
//        transaction4.setReference("REF126");
//        transaction4.setContext("Context4");
//        transaction4.setPrice(new Price(BigDecimal.valueOf(10), Currency.getInstance("MAD")));
//        transaction4.setStatus(TransactionStatus.COMPLETED);
//        transaction4.setType(TransactionType.CARD);
//        transaction4.setOrder(order2);
//
//        Transaction transaction5 = new Transaction();
//        transaction5.setPaymentId("PAY127");
//        transaction5.setReference("REF127");
//        transaction5.setContext("Context5");
//        transaction5.setPrice(new Price(BigDecimal.valueOf(10), Currency.getInstance("MAD")));
//        transaction5.setStatus(TransactionStatus.COMPLETED);
//        transaction5.setType(TransactionType.CARD);
//        transaction5.setOrder(order2);
//
//        order2.setTransactions(new ArrayList<>(Arrays.asList(transaction4, transaction5)));
//
//        // Save the orders
//        order1 = this.orderRepository.save(order1);
//        order2 = this.orderRepository.save(order2);
//        offer.getOrders().add(order1);
//        offer.getOrders().add(order2);
//        this.offerRepository.save(offer);
////        System.out.println("seed ");
//    }
////    @EventListener(ApplicationReadyEvent.class)
////    @Transactional
////    public void createOrga() {
////        System.out.println("seed ");
////        OrganizationEntity firstOrganization = this.organizationEntityRepository.findById(UUID.fromString("8e9837af-0bf4-495f-b3b5-a0bbdec6a252")).orElse(null);
////        UUID publisherId = firstOrganization.getId();
////
////        SubEntity subEntity = new SubEntity();
////
////        subEntity.setOrganizationEntity(firstOrganization);
////        subEntity.setType(SubEntityType.PARTNER_SB);
////        subEntity = this.subEntityRepository.save(subEntity);
////        PartnerCommissions partnerCommissions = PartnerCommissions.builder()
////                .partnerInfo(new PartnerInfo(firstOrganization.getId(), subEntity.getId(), subEntity.getPartnerType()))
////                .paymentStatus(PaymentStatus.IN_VALID)
////                .date(new Date())
////                .paymentResponsibility(firstOrganization.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
////                .parentPartner(firstOrganization.getCommissions().getFirst())
////                .build();
////        subEntity.getCommissions().add(partnerCommissions);
////        subEntity = this.subEntityRepository.save(subEntity);
////        PartnerCommissions p = firstOrganization.getCommissions().getFirst();
////        p.getSubEntityCommissions().add(subEntity.getCommissions().getFirst());
////        this.pr.save(p);
////        firstOrganization.getSubEntities().add(subEntity);
////        this.organizationEntityRepository.save(firstOrganization);
////
////        // Create Offer
////        Offer offer = new Offer();
////        offer.setPrice(new Price(BigDecimal.valueOf(100), Currency.getInstance("MAD")));
////        offer.setSalePrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
////        offer.setReduction(20);
////        offer.setPublisherInfo(new PublisherInfo(subEntity.getId(), subEntity.getPublisherType()));
////        offer = this.offerRepository.save(offer);
////
////        // Create Order 1
////        Order order1 = new Order();
////        order1.setPrice(new Price(BigDecimal.valueOf(50), Currency.getInstance("MAD")));
////        order1.setOffer(offer);  // Attach the order to the offer
////
////        // Create Order 2
////        Order order2 = new Order();
////        order2.setPrice(new Price(BigDecimal.valueOf(30), Currency.getInstance("MAD")));
////        order2.setOffer(offer);  // Attach the order to the offer
////
////        // Create Transaction for Order 1
////        Transaction transaction1 = new Transaction();
////        transaction1.setPaymentId("PAY123");
////        transaction1.setReference("REF123");
////        transaction1.setContext("Context1");
////        transaction1.setPrice(new Price(BigDecimal.valueOf(20), Currency.getInstance("MAD")));
////        transaction1.setStatus(TransactionStatus.COMPLETED);
////        transaction1.setType(TransactionType.CARD);
////        transaction1.setOrder(order1);  // Attach transaction to order
////
////
////
////        order1.setTransactions(new ArrayList<>(Arrays.asList(transaction1)));
////
////        // Create Transaction for Order 2
////        Transaction transaction4 = new Transaction();
////        transaction4.setPaymentId("PAY126");
////        transaction4.setReference("REF126");
////        transaction4.setContext("Context4");
////        transaction4.setPrice(new Price(BigDecimal.valueOf(10), Currency.getInstance("MAD")));
////        transaction4.setStatus(TransactionStatus.COMPLETED);
////        transaction4.setType(TransactionType.CARD);
////        transaction4.setOrder(order2);
////
////        order2.setTransactions(new ArrayList<>(Arrays.asList(transaction4)));
////
////        // Save the orders
////        order1 = this.orderRepository.save(order1);
////        order2 = this.orderRepository.save(order2);
////        offer.getOrders().add(order1);
////        offer.getOrders().add(order2);
////        this.offerRepository.save(offer);
////        System.out.println("seed ");
////    }
//////
//////    @EventListener(ApplicationReadyEvent.class)
//////    @Transactional
//////    public void createseed() {
//////        // Get the first organization to set as the publisher
////////        System.out.println("seed ");
////////        OrganizationEntity firstOrganization = this.organizationEntityRepository.findById(UUID.fromString("8890247a-f4ea-46f3-850f-afb5e1e4fa37")).orElse(null);
////////        UUID publisherId = firstOrganization.getId();
////////
////////        SubEntity subEntity = new SubEntity();
////////
////////        subEntity.setOrganizationEntity(firstOrganization);
////////        subEntity.setType(SubEntityType.PARTNER_SB);
////////        subEntity = this.subEntityRepository.save(subEntity);
////////        PartnerCommissions partnerCommissions = PartnerCommissions.builder()
////////                .partnerInfo(new PartnerInfo(subEntity.getId(), subEntity.getPartnerType()))
////////                .paymentStatus(PaymentStatus.IN_VALID)
////////                .date(new Date())
////////                .build();
////////        subEntity.getCommissions().add(partnerCommissions);
////////        this.subEntityRepository.save(subEntity);
////////        firstOrganization.getSubEntities().add(subEntity);
////////        this.organizationEntityRepository.save(firstOrganization);
////////
////////        // Create Offer
////////        Offer offer = new Offer();
////////        offer.setPrice(new Price(BigDecimal.valueOf(100), Currency.getInstance("MAD")));
////////        offer.setSalePrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
////////        offer.setReduction(20);
////////        offer.setPublisherInfo(new PublisherInfo(subEntity.getId(), subEntity.getPublisherType()));
////////        offer = this.offerRepository.save(offer);
////////
////////        // Create Order 1
////////        Order order1 = new Order();
////////        order1.setPrice(new Price(BigDecimal.valueOf(50), Currency.getInstance("MAD")));
////////        order1.setOffer(offer);  // Attach the order to the offer
////////
////////        // Create Order 2
////////        Order order2 = new Order();
////////        order2.setPrice(new Price(BigDecimal.valueOf(30), Currency.getInstance("MAD")));
////////        order2.setOffer(offer);  // Attach the order to the offer
////////
////////        // Create Transaction for Order 1
////////        Transaction transaction1 = new Transaction();
////////        transaction1.setPaymentId("PAY123");
////////        transaction1.setReference("REF123");
////////        transaction1.setContext("Context1");
////////        transaction1.setPrice(new Price(BigDecimal.valueOf(20), Currency.getInstance("MAD")));
////////        transaction1.setStatus(TransactionStatus.COMPLETED);
////////        transaction1.setType(TransactionType.CARD);
////////        transaction1.setOrder(order1);  // Attach transaction to order
////////
////////        Transaction transaction2 = new Transaction();
////////        transaction2.setPaymentId("PAY124");
////////        transaction2.setReference("REF124");
////////        transaction2.setContext("Context2");
////////        transaction2.setPrice(new Price(BigDecimal.valueOf(15), Currency.getInstance("MAD")));
////////        transaction2.setStatus(TransactionStatus.COMPLETED);
////////        transaction2.setType(TransactionType.CARD);
////////        transaction2.setOrder(order1);
////////
////////        Transaction transaction3 = new Transaction();
////////        transaction3.setPaymentId("PAY125");
////////        transaction3.setReference("REF125");
////////        transaction3.setContext("Context3");
////////        transaction3.setPrice(new Price(BigDecimal.valueOf(15), Currency.getInstance("MAD")));
////////        transaction3.setStatus(TransactionStatus.COMPLETED);
////////        transaction3.setType(TransactionType.CARD);
////////        transaction3.setOrder(order1);
////////
////////        order1.setTransactions(new ArrayList<>(Arrays.asList(transaction1, transaction2, transaction3)));
////////
////////        // Create Transaction for Order 2
////////        Transaction transaction4 = new Transaction();
////////        transaction4.setPaymentId("PAY126");
////////        transaction4.setReference("REF126");
////////        transaction4.setContext("Context4");
////////        transaction4.setPrice(new Price(BigDecimal.valueOf(10), Currency.getInstance("MAD")));
////////        transaction4.setStatus(TransactionStatus.COMPLETED);
////////        transaction4.setType(TransactionType.CARD);
////////        transaction4.setOrder(order2);
////////
////////        Transaction transaction5 = new Transaction();
////////        transaction5.setPaymentId("PAY127");
////////        transaction5.setReference("REF127");
////////        transaction5.setContext("Context5");
////////        transaction5.setPrice(new Price(BigDecimal.valueOf(10), Currency.getInstance("MAD")));
////////        transaction5.setStatus(TransactionStatus.COMPLETED);
////////        transaction5.setType(TransactionType.CARD);
////////        transaction5.setOrder(order2);
////////
////////        order2.setTransactions(new ArrayList<>(Arrays.asList(transaction4, transaction5)));
////////
////////        // Save the orders
////////        order1 = this.orderRepository.save(order1);
////////        order2 = this.orderRepository.save(order2);
////////        offer.getOrders().add(order1);
////////        offer.getOrders().add(order2);
////////        this.offerRepository.save(offer);
////////        System.out.println("seed ");
//    }
//////
//////
//////}
//////
//////
////////// case of subentity don't pay
////////// case of subentity pay.