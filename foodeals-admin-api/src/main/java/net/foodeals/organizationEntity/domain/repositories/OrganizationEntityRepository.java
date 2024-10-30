package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationEntityRepository extends BaseRepository<OrganizationEntity, UUID> {
    Page<OrganizationEntity> findByType(EntityType type, Pageable pageable);

    @Query("SELECT o FROM OrganizationEntity o WHERE o.type IN (:types)")
    Page<OrganizationEntity> findByType(List<EntityType> types, Pageable pageable);

    Page<OrganizationEntity> findByDeletedAtIsNotNull(Pageable pageable);
    Optional<OrganizationEntity> findByIdAndDeletedAtIsNotNull(UUID uuid);
    Page<OrganizationEntity> findByDeletedAtIsNotNullAndType(Pageable pageable, EntityType type);

    Page<OrganizationEntity> findByTypeIn(List<EntityType> entityTypes, Pageable pageable);
    Page<OrganizationEntity> findByTypeInAndSolutionsContainingAndContractContractStatus(
            List<EntityType> types,
            Solution solution,
            ContractStatus contractStatus, Pageable pageable
    );

    @Query("SELECT o.id FROM OrganizationEntity o " +

            "WHERE o.type IN :types " +

            "AND :solution MEMBER OF o.solutions " +

            "AND o.contract.contractStatus = :status")

    List<UUID> findPartnerIds(

            List<EntityType> types,

            Solution solution,

            ContractStatus status

    );
}
