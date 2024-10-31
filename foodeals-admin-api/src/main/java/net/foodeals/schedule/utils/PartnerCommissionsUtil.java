package net.foodeals.schedule.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerCommissionsUtil {

    private final EntityManager entityManager;
    private final OrganizationEntityRepository organizationRepository;
    private final SolutionService solutionService;

    @Transactional
    public void createMonthlyPartnerCommissions() {
        Solution proMarket = solutionService.findByName("pro_market");
        List<UUID> partnerIds = organizationRepository.findPartnerIds(List.of(EntityType.NORMAL_PARTNER, EntityType.PARTNER_WITH_SB), proMarket, ContractStatus.VALIDATED);

        int batchSize = 100;
        for (int i = 0; i < partnerIds.size(); i += batchSize) {
            List<UUID> batchIds = partnerIds.subList(i, Math.min(i + batchSize, partnerIds.size()));
            List<OrganizationEntity> partners = organizationRepository.findAllById(batchIds);
            for (OrganizationEntity partner : partners) {
                try {
                    createCommissionsForPartner(partner);
                    log.error("Successfully processed partner {}", partner.getId());
                } catch (Exception e) {
                    log.error("Error creating commission for partner {}: {}", partner.getId(), e.getMessage());
                }
            }
        }
    }

    @Transactional
    public void createCommissionsForPartner(OrganizationEntity partner) {
        Date date = new Date();
        if (partner.getType() == EntityType.NORMAL_PARTNER) {
            createCommissionForNormalPartner(partner, date);
        } else if (partner.getType() == EntityType.PARTNER_WITH_SB) {
            createCommissionForPartnerWithSB(partner, date);
        }
    }

    private void createCommissionForNormalPartner(OrganizationEntity partner, Date date) {
        OrganizationEntity freshPartner = entityManager.find(OrganizationEntity.class, partner.getId(), LockModeType.PESSIMISTIC_WRITE);
        PartnerCommissions partnerCommissions = PartnerCommissions.builder()
                .partnerInfo(new PartnerInfo(freshPartner.getId(), freshPartner.getId(), freshPartner.getPartnerType()))
                .paymentStatus(PaymentStatus.IN_VALID)
                .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                .date(date)
                .build();

        entityManager.persist(partnerCommissions);
        freshPartner.getCommissions().add(partnerCommissions);
        entityManager.merge(freshPartner);
    }

    private void createCommissionForPartnerWithSB(OrganizationEntity partner, Date date) {
        OrganizationEntity freshPartner = entityManager.find(OrganizationEntity.class, partner.getId(), LockModeType.PESSIMISTIC_WRITE);
        PartnerCommissions parentCommission = PartnerCommissions.builder()
                .partnerInfo(new PartnerInfo(freshPartner.getId(), freshPartner.getId(), freshPartner.getPartnerType()))
                .paymentStatus(PaymentStatus.IN_VALID)
                .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                .date(date)
                .build();

        entityManager.persist(parentCommission);
        freshPartner.getCommissions().add(parentCommission);
        entityManager.merge(freshPartner);

        for (SubEntity subEntity : freshPartner.getSubEntities()) {
            SubEntity freshSubEntity = entityManager.find(SubEntity.class, subEntity.getId(), LockModeType.PESSIMISTIC_WRITE);
            PartnerCommissions subEntityCommission = PartnerCommissions.builder()
                    .partnerInfo(new PartnerInfo(freshPartner.getId(), freshSubEntity.getId(), freshSubEntity.getPartnerType()))
                    .paymentStatus(PaymentStatus.IN_VALID)
                    .date(date)
                    .paymentResponsibility(freshPartner.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                    .parentPartner(parentCommission)
                    .build();

            entityManager.persist(subEntityCommission);
            freshSubEntity.getCommissions().add(subEntityCommission);
            parentCommission.getSubEntityCommissions().add(subEntityCommission);
            entityManager.merge(freshSubEntity);
        }
    }
}
