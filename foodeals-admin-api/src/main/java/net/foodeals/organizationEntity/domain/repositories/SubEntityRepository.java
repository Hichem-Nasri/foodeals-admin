package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubEntityRepository extends BaseRepository<SubEntity, UUID> {
    Integer countByOrganizationEntity_IdAndType(UUID organizationId, SubEntityType type);
    Page<SubEntity> findByOrganizationEntity_Id(UUID id, Pageable pageable);
}
