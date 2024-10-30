//package net.foodeals.schedule;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.LockModeType;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import net.foodeals.contract.domain.entities.enums.ContractStatus;
//import net.foodeals.organizationEntity.application.services.SolutionService;
//import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
//import net.foodeals.organizationEntity.domain.entities.Solution;
//import net.foodeals.organizationEntity.domain.entities.SubEntity;
//import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
//import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
//import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
//import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
//import net.foodeals.payment.domain.entities.PartnerCommissions;
//import net.foodeals.payment.domain.entities.PartnerInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@Slf4j
//public class PartnerCommissionsScheduler {
//    @Autowired
//    private OrganizationEntityRepository organizationRepository;
//    @Autowired
//    private SolutionService solutionService;
//    @Autowired
//    private EntityManager entityManager;
//
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//    @PostConstruct
//    public void init() {
//        scheduler.schedule(this::createMonthlyPartnerCommissions, 30, TimeUnit.SECONDS);
//    }
//
//    @PreDestroy
//    public void destroy() {
//        scheduler.shutdownNow();
//    }
//
//    @Scheduled(cron = "0 0 0 1 * ?")
//    @Transactional
//    public void createMonthlyPartnerCommissions() {
//        log.error("Starting monthly partner commissions creation");
//        try {
//            this.start();
//        } catch (Exception e) {
//            log.error("Error in monthly partner commissions creation", e);
//        }
//        log.error("Finished monthly partner commissions creation");
//    }
//
//    @Transactional
//    private void start() {
//        Solution proMarket = solutionService.findByName("pro_market");
//        List<UUID> partnerIds = organizationRepository.findPartnerIds(Arrays.asList(EntityType.NORMAL_PARTNER, EntityType.PARTNER_WITH_SB), proMarket, ContractStatus.VALIDATED);
//        log.error("Found {} partners to process", partnerIds.size());
//        int batchSize = 100;
//        for (int i = 0; i < partnerIds.size(); i += batchSize) {
//            int endIndex = Math.min(i + batchSize, partnerIds.size());
//            List<UUID> batchIds = partnerIds.subList(i, endIndex);
//            List<OrganizationEntity> partners = organizationRepository.findAllById(batchIds);
//            for (OrganizationEntity partner : partners) {
//                try {
//                    createCommissionsForPartner(partner);
//                    log.error("Successfully processed partner {}", partner.getId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.error("Error creating commission for partner {}: {}", partner.getId(), e.getMessage());
//                }
//            }
//        }
//    }
//
//    @Transactional
//    private void createCommissionsForPartner(OrganizationEntity partner) {
//        Date date = new Date();
//        if (partner.getType() == EntityType.NORMAL_PARTNER) {
//            createCommissionForNormalPartner(partner, date);
//        } else if (partner.getType() == EntityType.PARTNER_WITH_SB) {
//            createCommissionForPartnerWithSB(partner, date);
//        }
//    }
//
//    @Transactional
//    private void createCommissionForNormalPartner(OrganizationEntity partner, Date date) {
//        entityManager.clear();
//
//        // Fetch a fresh instance of the partner
//        OrganizationEntity freshPartner = entityManager.find(
//                OrganizationEntity.class,
//                partner.getId(),
//                LockModeType.PESSIMISTIC_WRITE
//        );
//
//        PartnerCommissions partnerCommissions = PartnerCommissions.builder()
//                .partnerInfo(new PartnerInfo(freshPartner.getId(), freshPartner.getId(), freshPartner.getPartnerType()))
//                .paymentStatus(PaymentStatus.IN_VALID)
//                .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ?
//                        PaymentResponsibility.PAYED_BY_SUB_ENTITIES :
//                        PaymentResponsibility.PAYED_BY_PARTNER)
//                .date(date)
//                .build();
//
//        // Save commission first
//        entityManager.persist(partnerCommissions);
//        entityManager.flush();
//
//        // Update partner's commissions
//        freshPartner.getCommissions().add(partnerCommissions);
//        entityManager.merge(freshPartner);
//        entityManager.flush();
//    }
//
//    @Transactional
//    private void createCommissionForPartnerWithSB(OrganizationEntity partner, Date date) {
//        entityManager.clear();
//
//        // Fetch fresh instance
//        OrganizationEntity freshPartner = entityManager.find(
//                OrganizationEntity.class,
//                partner.getId(),
//                LockModeType.PESSIMISTIC_WRITE
//        );
//
//        PartnerCommissions parentCommission = PartnerCommissions.builder()
//                .partnerInfo(new PartnerInfo(freshPartner.getId(), freshPartner.getId(), freshPartner.getPartnerType()))
//                .paymentStatus(PaymentStatus.IN_VALID)
//                .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ?
//                        PaymentResponsibility.PAYED_BY_SUB_ENTITIES :
//                        PaymentResponsibility.PAYED_BY_PARTNER)
//                .date(date)
//                .build();
//
//        // Save parent commission
//        entityManager.persist(parentCommission);
//        entityManager.flush();
//
//        // Add to partner
//        freshPartner.getCommissions().add(parentCommission);
//        entityManager.merge(freshPartner);
//        entityManager.flush();
//
//        // Process sub entities
//        for (SubEntity subEntity : freshPartner.getSubEntities()) {
//            SubEntity freshSubEntity = entityManager.find(
//                    SubEntity.class,
//                    subEntity.getId(),
//                    LockModeType.PESSIMISTIC_WRITE
//            );
//
//            PartnerCommissions subEntityCommission = PartnerCommissions.builder()
//                    .partnerInfo(new PartnerInfo(freshPartner.getId(), freshSubEntity.getId(), freshSubEntity.getPartnerType()))
//                    .paymentStatus(PaymentStatus.IN_VALID)
//                    .date(date)
//                    .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ?
//                            PaymentResponsibility.PAYED_BY_SUB_ENTITIES :
//                            PaymentResponsibility.PAYED_BY_PARTNER)
//                    .parentPartner(parentCommission)
//                    .build();
//
//            // Save sub entity commission
//            entityManager.persist(subEntityCommission);
//
//            // Update relationships
//            freshSubEntity.getCommissions().add(subEntityCommission);
//            parentCommission.getSubEntityCommissions().add(subEntityCommission);
//
//            entityManager.merge(freshSubEntity);
//        }
//
//        // Final flush
//        entityManager.flush();
//    }
//}