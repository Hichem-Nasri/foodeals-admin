package net.foodeals.crm.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProspectRepository extends BaseRepository<Prospect, UUID> {

    Integer countByLeadIsNotNullAndStatusAndDeletedAtIsNull(ProspectStatus status);

    Integer countByStatusAndDeletedAtIsNull(ProspectStatus status);
}
