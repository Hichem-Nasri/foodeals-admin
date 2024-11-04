//package net.foodeals.payment.infrastructure;
//
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import net.foodeals.common.annotations.Seeder;
//import net.foodeals.contract.application.service.DeadlinesService;
//import net.foodeals.contract.application.service.SubscriptionService;
//import net.foodeals.contract.domain.entities.Deadlines;
//import net.foodeals.contract.domain.entities.SolutionContract;
//import net.foodeals.contract.domain.entities.Subscription;
//import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
//import net.foodeals.organizationEntity.domain.entities.Solution;
//import net.foodeals.organizationEntity.domain.entities.SubEntity;
//import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
//import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
//import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
//import net.foodeals.payment.domain.entities.Enum.PartnerType;
//import net.foodeals.payment.domain.entities.PartnerInfo;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//@AllArgsConstructor
//public class SubEntitySeeder {
//    private final OrganizationEntityRepository organizationEntityRepository;
//    private final SubEntityRepository subEntityRepository;
//    private final SubscriptionService subscriptionService;
//    private final DeadlinesService deadlinesService;
//
//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void seed() {
//        String organizationEntityId = "4bb1c6ac-929b-4d25-89d0-97ad7a093756";
//
//        OrganizationEntity organizationEntity = organizationEntityRepository.findById(UUID.fromString(organizationEntityId))
//                .orElseThrow(() -> new RuntimeException("Organization not found"));
////
//      createSubEntity(organizationEntity, "Sub Entity 1", SubEntityType.PARTNER_SB);
//    }
//
//    @Transactional
//    private SubEntity createSubEntity(OrganizationEntity organizationEntity, String subEntityName, SubEntityType type) {
//        SubEntity subEntity = SubEntity.builder()
//                .name(subEntityName)
//                .type(type)
//                .organizationEntity(organizationEntity)
//                .contract(organizationEntity.getContract())
//                .subscriptions(new ArrayList<>())
//                .build();
//
//        subEntity = subEntityRepository.save(subEntity);
//        createSubEntitySubscriptions(organizationEntity, subEntity);
//        return subEntityRepository.save(subEntity); // Save again with subscriptions
//    }
//
//    @Transactional
//    private void createSubEntitySubscriptions(OrganizationEntity organizationEntity, SubEntity subEntity) {
//        if (organizationEntity.getContract().isSingleSubscription()) {
//            createSingleSubscription(organizationEntity, subEntity);
//        } else {
//            createMultipleSubscriptions(organizationEntity, subEntity);
//        }
//    }
//
//    @Transactional
//    private void createSingleSubscription(OrganizationEntity organizationEntity, SubEntity subEntity) {
//        Subscription parentSubscription = organizationEntity.getSubscriptions().get(0);
//        Subscription subEntitySubscription = createSubEntitySubscription(parentSubscription, subEntity);
//
//        Set<Solution> solutions = organizationEntity.getSubscriptions().stream()
//                .flatMap(subscription -> subscription.getSolutionContracts().stream())
//                .map(SolutionContract::getSolution)
//                .collect(Collectors.toSet());
//        subEntitySubscription.setSolutions(solutions);
//
//        // Save subscription first
//        subEntitySubscription = subscriptionService.save(subEntitySubscription);
//
//        // Update and save parent subscription
//        parentSubscription.getSubEntities().add(subEntitySubscription);
//        subscriptionService.save(parentSubscription);
//
//        // Update and save subEntity
//        subEntity.getSubscriptions().add(subEntitySubscription);
//        subEntityRepository.save(subEntity);
//
//        // Final save of subEntity subscription
//        subscriptionService.save(subEntitySubscription);
//    }
//
//    private void createMultipleSubscriptions(OrganizationEntity organizationEntity, SubEntity subEntity) {
//        // Create a new list to avoid concurrent modification
//        List<Subscription> parentSubscriptions = new ArrayList<>(organizationEntity.getSubscriptions());
//
//        for (Subscription parentSubscription : parentSubscriptions) {
//            Subscription subEntitySubscription = createSubEntitySubscription(parentSubscription, subEntity);
//
//            Set<Solution> solutions = parentSubscription.getSolutionContracts().stream()
//                    .map(SolutionContract::getSolution)
//                    .collect(Collectors.toSet());
//            subEntitySubscription.setSolutions(solutions);
//
//            // Save subscription first
//            subEntitySubscription = subscriptionService.save(subEntitySubscription);
//
//            // Update relationships
//            parentSubscription.getSubEntities().add(subEntitySubscription);
//            subEntity.getSubscriptions().add(subEntitySubscription);
//
//            // Save all entities
//            subscriptionService.save(parentSubscription);
//        }
//
//        // Final save of subEntity with all its subscriptions
//        subEntityRepository.save(subEntity);
//    }
//
//    @Transactional
//    private Subscription createSubEntitySubscription(Subscription parentSubscription, SubEntity subEntity) {
//        Subscription subEntitySubscription = Subscription.builder()
//                .amount(parentSubscription.getAmount())
//                .numberOfDueDates(parentSubscription.getNumberOfDueDates())
//                .startDate(parentSubscription.getStartDate())
//                .duration(parentSubscription.getDuration())
//                .endDate(parentSubscription.getEndDate())
//                .subscriptionStatus(parentSubscription.getSubscriptionStatus())
//                .partner(new PartnerInfo(subEntity.getOrganizationEntity().getId(),
//                        subEntity.getId(),
//                        PartnerType.SUB_ENTITY))
//                .parentPartner(parentSubscription)
//                .solutions(new HashSet<>())
//                .subEntities(new HashSet<>())
//                .deadlines(new ArrayList<>())
//                .build();
//
//        // Save initial subscription
//        subEntitySubscription = subscriptionService.save(subEntitySubscription);
//
//        // Create and save deadlines
//        Subscription finalSubEntitySubscription = subEntitySubscription;
//        List<Deadlines> deadlines = parentSubscription.getDeadlines().stream()
//                .map((Deadlines deadline) -> createSubEntityDeadline(deadline, finalSubEntitySubscription))
//                .collect(Collectors.toList());
//
//        subEntitySubscription.setDeadlines(deadlines);
//        return subscriptionService.save(subEntitySubscription);
//    }
//
//    @Transactional
//    private Deadlines createSubEntityDeadline(Deadlines parentDeadline, Subscription subEntitySubscription) {
//        Deadlines subEntityDeadline = Deadlines.builder()
//                .subscription(subEntitySubscription)
//                .amount(parentDeadline.getAmount())
//                .dueDate(parentDeadline.getDueDate())
//                .status(parentDeadline.getStatus())
//                .paymentMethod(parentDeadline.getPaymentMethod())
//                .paymentResponsibility(parentDeadline.getPaymentResponsibility())
//                .parentPartner(parentDeadline)
//                .subEntityDeadlines(new HashSet<>())
//                .build();
//
//        subEntityDeadline = deadlinesService.save(subEntityDeadline);
//
//        parentDeadline.getSubEntityDeadlines().add(subEntityDeadline);
//        deadlinesService.save(parentDeadline);
//
//        return subEntityDeadline;
//    }
//}
