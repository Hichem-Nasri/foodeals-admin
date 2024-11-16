package net.foodeals.payment.domain.repository;


import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerCommissionsRepository extends JpaRepository<PartnerCommissions, UUID> {
    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    List<PartnerCommissions> findCommissionsByDate(@Param("year") int year, @Param("month") int month);

    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year " +
            "AND EXTRACT(MONTH FROM s.date) = :month " +
            "AND s.partnerInfo.organizationId = :organizationId AND s.partnerInfo.type != :type" )
    List<PartnerCommissions> findCommissionsByDateAndOrganizationAndPartnerInfoTypeNot(
            @Param("year") int year,
            @Param("month") int month,
            @Param("organizationId") UUID organizationId,
            @Param("type") PartnerType type
    );
        @Query("SELECT p FROM PartnerCommissions p WHERE p.partnerInfo.name LIKE %:name%")
        Page<PartnerCommissions> findByPartnerName(String name, Pageable pageable);

    @Query("SELECT pc FROM PartnerCommissions pc WHERE YEAR(pc.date) = :year AND MONTH(pc.date) = :month AND pc.partnerInfo.id = :partnerId")
    PartnerCommissions findCommissionsByDateAndPartnerInfoId(
            @Param("year") int year,
            @Param("month") int month,
            @Param("partnerId") UUID partnerId
    );

    @Query("SELECT pc FROM PartnerCommissions pc WHERE  pc.partnerInfo.id = :partnerId")
    Optional<PartnerCommissions> findCommissionsByPartnerInfoId(
            @Param("partnerId") UUID partnerId
    );

    @Query("SELECT p FROM PartnerCommissions p WHERE p.partnerInfo.name LIKE %:name% AND p.partnerInfo.type IN :types")
    Page<PartnerCommissions> findByPartnerNameAndTypeIn(String name, List<PartnerType> types, Pageable pageable);


    @Query("SELECT pc FROM PartnerCommissions pc WHERE YEAR(pc.date) = :year AND MONTH(pc.date) = :month AND pc.partnerInfo.type != :type")
    List<PartnerCommissions> findCommissionsByDateAndPartnerInfoTypeNot(
            @Param("year") int year,
            @Param("month") int month,
            @Param("type") PartnerType type
            );

    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year " +
            "AND s.partnerInfo.organizationId = :organizationId")
    List<PartnerCommissions> findCommissionsByDateAndOrganization(
            @Param("year") int year,
            @Param("organizationId") UUID organizationId
    );

        // Get distinct months without any filters
        @Query("SELECT DISTINCT TO_CHAR(p.date, 'YYYY-MM') FROM PartnerCommissions p")
        List<String> findDistinctMonths();

        // Get distinct months by partnerId only
        @Query("SELECT DISTINCT TO_CHAR(p.date, 'YYYY-MM') " +
                "FROM PartnerCommissions p " +
                "WHERE p.partnerInfo.id = :partnerId")
        List<String> findDistinctMonthsByPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT DISTINCT EXTRACT(YEAR FROM p.date) " +
            "FROM PartnerCommissions p " +
            "WHERE p.partnerInfo.id = :partnerId")
    List<Integer> findDistinctYearsByPartner(@Param("partnerId") UUID partnerId);

        // Get distinct months by organizationId only
        @Query("SELECT DISTINCT TO_CHAR(p.date, 'YYYY-MM') " +
                "FROM PartnerCommissions p " +
                "WHERE p.partnerInfo.organizationId = :organizationId")
        List<String> findDistinctMonthsByOrganization(@Param("organizationId") UUID organizationId);

    }
