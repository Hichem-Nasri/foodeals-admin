package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.location.domain.entities.City;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityFilter;
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

    @Query("SELECT DISTINCT o FROM OrganizationEntity o " +
            "LEFT JOIN o.solutions s " +
            "LEFT JOIN o.address.region.city c " +
            "LEFT JOIN o.users u " +
            "JOIN o.contacts ct " +
            "LEFT JOIN o.contract con " + // Join with the Contract entity
            "WHERE (coalesce(:#{#filter.startDate}, null) IS NULL OR o.createdAt >= :#{#filter.startDate}) " +
            "AND (coalesce(:#{#filter.endDate}, null) IS NULL OR o.createdAt <= :#{#filter.endDate}) " +
            "AND (:#{#filter.types} IS NULL OR o.type IN :#{#filter.types}) " +
            "AND (:#{#filter.names} IS NULL OR o.name IN :#{#filter.names}) " +
            "AND (:#{#filter.solutions} IS NULL OR s.name IN :#{#filter.solutions}) " +
            "AND (:#{#filter.cityId} IS NULL OR c.id = :#{#filter.cityId}) " +
            "AND (:#{#filter.email} IS NULL OR ct.email = :#{#filter.email}) " +
            "AND (:#{#filter.phone} IS NULL OR ct.phone = :#{#filter.phone}) " +
            "AND (:#{#filter.collabId} IS NULL OR u.id = :#{#filter.collabId}) " +
            "AND (:#{#filter.deletedAt} IS NULL OR (o.deletedAt IS NOT NULL AND :#{#filter.deletedAt} = true) OR (o.deletedAt IS NULL AND :#{#filter.deletedAt} = false)) " +
            "AND (:#{#filter.contractStatus} IS NULL OR con.contractStatus = :#{#filter.contractStatus})") // New condition for contract status
    Page<OrganizationEntity> findWithFilters(
            @Param("filter") OrganizationEntityFilter filter,
            Pageable pageable
    );


    @Query("SELECT DISTINCT c FROM OrganizationEntity o " +
            "JOIN o.address a " +
            "JOIN a.region r " +
            "JOIN r.city c " +
            "JOIN c.country co " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :cityName, '%')) " +
            "AND LOWER(co.name) = LOWER(:countryName)")
    Page<City> findCitiesByOrganizationAddress(String cityName, String countryName, Pageable pageable);

    Page<OrganizationEntity> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<OrganizationEntity> findByIdAndDeletedAtIsNotNull(UUID uuid);
    Page<OrganizationEntity> findByDeletedAtIsNotNullAndTypeIn(Pageable pageable, List<EntityType> type);

    @Query("SELECT o FROM OrganizationEntity o WHERE o.name LIKE %:name% AND o.type IN :types AND ((o.deletedAt IS NOT NULL AND :deleted = true) OR (o.deletedAt IS NULL AND :deleted = false))")
    Page<OrganizationEntity> findByNameContainingAndTypeInAndDeletedAtIs(
            @Param("name") String name,
            @Param("types") List<EntityType> types,
            @Param("deleted") boolean deleted,
            Pageable pageable
    );

@Query("SELECT o FROM OrganizationEntity o WHERE o.type IN :types AND ((o.deletedAt IS NOT NULL AND :deleted = true) OR (o.deletedAt IS NULL AND :deleted = false)) ")
Page<OrganizationEntity> findByTypeInAndDeletedAtIs(@Param("types") List<EntityType> types, @Param("deleted") boolean deleted, Pageable pageable);

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
