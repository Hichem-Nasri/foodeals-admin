package net.foodeals.crm.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProspectRepository extends BaseRepository<Prospect, UUID> {

    @Query("SELECT COUNT(DISTINCT p.lead.id) FROM Prospect p WHERE p.lead IS NOT NULL AND p.status = :status AND p.deletedAt IS NULL")
    Integer countDistinctLeadsByStatus(@Param("status") ProspectStatus status);

    Integer countByStatusAndDeletedAtIsNull(ProspectStatus status);
}
