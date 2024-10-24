package net.foodeals.contract.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CommissionRepository extends BaseRepository<Commission, UUID> {
    @Query("SELECT sc.commission FROM SolutionContract sc " +
            "JOIN sc.contract c " +
            "JOIN c.organizationEntity o " + // Join with OrganizationEntity
            "WHERE o.id = :organizationId") // Filter by organization ID
    Commission findCommissionByOrganizationId(@Param("organizationId") UUID organizationId);
}
