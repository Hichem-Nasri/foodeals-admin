package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.location.domain.entities.City;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityFilter;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubEntityRepository extends BaseRepository<SubEntity, UUID> {
    Integer countByOrganizationEntity_IdAndType(UUID organizationId, SubEntityType type);
    Page<SubEntity> findByOrganizationEntity_Id(UUID id, Pageable pageable);

    @Query("SELECT DISTINCT s FROM SubEntity s " +
            "JOIN s.organizationEntity o " +
            "LEFT JOIN s.contacts c " +
            "LEFT JOIN s.users u " +
            "LEFT JOIN o.address.region.city ci " +
            "WHERE o.id = :id " +
            "AND (coalesce(:#{#filter.startDate}, null) IS NULL OR s.createdAt >= :#{#filter.startDate}) " +
            "AND (coalesce(:#{#filter.endDate}, null) IS NULL OR s.createdAt <= :#{#filter.endDate}) " +
            "AND (:#{#filter.types} IS NULL OR s.type IN :#{#filter.types}) " +
            "AND (:#{#filter.names} IS NULL OR s.name IN :#{#filter.names}) " +
            "AND (:#{#filter.cityId} IS NULL OR ci.id = :#{#filter.cityId}) " +
            "AND (:#{#filter.email} IS NULL OR c.email = :#{#filter.email}) " +
            "AND (:#{#filter.phone} IS NULL OR c.phone = :#{#filter.phone}) " +
            "AND (:#{#filter.collabId} IS NULL OR u.id = :#{#filter.collabId}) " +
            "AND (:#{#filter.deletedAt} IS NULL OR (s.deletedAt IS NOT NULL AND :#{#filter.deletedAt} = true) OR (s.deletedAt IS NULL AND :#{#filter.deletedAt} = false))")
    Page<SubEntity> findWithFilters(
            @Param("id") UUID id,
            @Param("filter") SubEntityFilter filter,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM SubEntity s " +
            "JOIN s.organizationEntity o " +
            "JOIN s.address a " +
            "JOIN a.region r " +
            "JOIN r.city c " +
            "WHERE o.id = :organizationId " +
            "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :cityName, '%'))")
    Page<City> findCitiesByOrganizationIdAndCityName(@Param("organizationId") UUID organizationId, @Param("cityName") String cityName, Pageable pageable);

}
