package net.foodeals.payment.domain.repository;


import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PartnerCommissionsRepository extends JpaRepository<PartnerCommissions, UUID> {
    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    List<PartnerCommissions> findCommissionsByDate(@Param("year") int year, @Param("month") int month);

    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year " +
            "AND EXTRACT(MONTH FROM s.date) = :month " +
            "AND s.partnerInfo.organizationId = :organizationId")
    List<PartnerCommissions> findCommissionsByDateAndOrganization(
            @Param("year") int year,
            @Param("month") int month,
            @Param("organizationId") UUID organizationId
    );

    @Query("SELECT pc FROM PartnerCommissions pc WHERE YEAR(pc.date) = :year AND MONTH(pc.date) = :month AND pc.partnerInfo.id = :partnerId")
    PartnerCommissions findCommissionsByDateAndPartnerInfoId(
            @Param("year") int year,
            @Param("month") int month,
            @Param("partnerId") UUID partnerId
    );

    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year " +
            "AND s.partnerInfo.organizationId = :organizationId")
    List<PartnerCommissions> findCommissionsByDateAndOrganization(
            @Param("year") int year,
            @Param("organizationId") UUID organizationId
    );

}
