package net.foodeals.payment.domain.repository;


import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PartnerCommissionsRepository extends JpaRepository<PartnerCommissions, UUID> {
    @Query("SELECT s FROM PartnerCommissions s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    Page<PartnerCommissions> findCommissionsByDate(@Param("year") int year, @Param("month") int month, Pageable pageable);
}
