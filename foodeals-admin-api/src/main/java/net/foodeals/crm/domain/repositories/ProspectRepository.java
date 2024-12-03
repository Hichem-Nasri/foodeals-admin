package net.foodeals.crm.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.crm.application.dto.responses.ProspectFilter;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProspectRepository extends BaseRepository<Prospect, UUID> {

    @Query("SELECT COUNT(DISTINCT p.lead.id) FROM Prospect p WHERE p.lead IS NOT NULL AND p.status = :status AND p.deletedAt IS NULL")
    Integer countDistinctLeadsByStatus(@Param("status") ProspectStatus status);

    Integer countByStatusAndDeletedAtIsNull(ProspectStatus status);

    @Query("SELECT DISTINCT p FROM Prospect p " +
            "LEFT JOIN p.activities a " +
            "JOIN p.address.region.city c " +
            "JOIN c.state.country co " +
            "JOIN p.contacts ct " +
            "WHERE (coalesce(:#{#filter.startDate}, null) IS NULL OR p.createdAt >= :#{#filter.startDate}) " +
            "AND (coalesce(:#{#filter.endDate}, null) IS NULL OR p.createdAt <= :#{#filter.endDate}) " +
            "AND (:#{#filter.names} IS NULL OR p.name IN :#{#filter.names}) " +
            "AND (:#{#filter.categories} IS NULL OR " +
            "(SELECT COUNT(DISTINCT a.name) FROM Activity a JOIN a.prospects p2 " +
            "WHERE p2 = p AND a.name IN :#{#filter.categories}) = :#{#filter.categories.size()}) " +
            "AND (:#{#filter.cityId} IS NULL OR c.id = :#{#filter.cityId}) " +
            "AND (:#{#filter.countryId} IS NULL OR co.id = :#{#filter.countryId}) " +
            "AND (:#{#filter.creatorId} IS NULL OR p.creator.id = :#{#filter.creatorId}) " +
            "AND (:#{#filter.leadId} IS NULL OR p.lead.id = :#{#filter.leadId}) " +
            "AND (:#{#filter.email} IS NULL OR ct.email = :#{#filter.email}) " +
            "AND (:#{#filter.phone} IS NULL OR ct.phone = :#{#filter.phone}) " +
            "AND (:#{#filter.statuses} IS NULL OR p.status IN :#{#filter.statuses})")
    Page<Prospect> findAllWithFilters(
            @Param("filter") ProspectFilter filter,
            Pageable pageable
    );

    @Query("SELECT p FROM Prospect p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.type IN :types AND ((p.deletedAt IS NOT NULL AND :deleted = true) OR (p.deletedAt IS NULL AND :deleted = false))")
    Page<Prospect> findByNameContainingAndTypeInAndDeletedAtIs(
            @Param("name") String name,
            @Param("types") List<ProspectType> types,
            @Param("deleted") boolean deleted,
            Pageable pageable
    );

    @Query("SELECT p FROM Prospect p WHERE p.type IN :types AND ((p.deletedAt IS NOT NULL AND :deleted = true) OR (p.deletedAt IS NULL AND :deleted = false))")
    Page<Prospect> findByTypeInAndDeletedAtIs(@Param("types") List<ProspectType> types, @Param("deleted") boolean deleted, Pageable pageable);
    // Count prospects by status and type, excluding deleted ones
    Long countByStatusAndTypeInAndDeletedAtIsNull(ProspectStatus status, List<ProspectType> type);

    // Count prospects by type, excluding deleted ones
    Long countByTypeIn(List<ProspectType> type);


    @Query("SELECT DISTINCT c FROM Prospect p " +
            "JOIN p.address a " +
            "JOIN a.region r " +
            "JOIN r.city c " +
            "JOIN c.state s " +
            "JOIN s.country co " +
            "WHERE p.type IN (:types) " +
            "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :cityName, '%')) " +
            "AND LOWER(co.name) = LOWER(:countryName)")
    Page<City> findCitiesByProspectAddress(@Param("types") List<ProspectType> types, @Param("cityName") String cityName, @Param("countryName") String countryName, Pageable pageable);

    @Query("SELECT DISTINCT r FROM Prospect p " +
            "JOIN p.address a " +
            "JOIN a.region r " +
            "JOIN r.city c " +
            "JOIN c.state s " +
            "JOIN s.country co " +
            "WHERE p.type IN (:types) " +
            "AND LOWER(r.name) LIKE LOWER(CONCAT('%', :regionName, '%')) " +
            "AND LOWER(co.name) = LOWER(:countryName)")
    Page<Region> findRegionsByProspectAddress(@Param("types") List<ProspectType> types, @Param("regionName") String regionName, @Param("countryName") String countryName, Pageable pageable);
}