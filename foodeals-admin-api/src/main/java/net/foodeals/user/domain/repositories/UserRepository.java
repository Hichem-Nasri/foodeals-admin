package net.foodeals.user.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.user.application.dtos.requests.UserFilter;
import net.foodeals.user.application.dtos.responses.UserSearchOrganizationFilter;
import net.foodeals.user.application.dtos.responses.UserSearchSubentityFilters;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User, Integer> {


    @Query("SELECT u FROM User u " +
            "JOIN u.organizationEntity oe " +
            "WHERE (:#{#filter.query} IS NULL OR CONCAT(u.name.firstName, ' ', u.name.lastName) LIKE CONCAT('%', :#{#filter.query}, '%')) " +
            "AND (:#{#filter.types} IS NULL OR oe.type IN :#{#filter.types}) ")
    Page<User> findWithFiltersOrganization(
            @Param("filter") UserSearchOrganizationFilter filter,
            Pageable pageable
    );

    @Query("SELECT u FROM User u " +
            "JOIN u.subEntity se " +
            "WHERE (:#{#filter.query} IS NULL OR CONCAT(u.name.firstName, ' ', u.name.lastName) LIKE CONCAT('%', :#{#filter.query}, '%')) " +
            "AND (:#{#filter.organizationId} IS NULL OR se.organizationEntity.id = :#{#filter.organizationId}) ")
    Page<User> findWithFiltersSubentity(
            @Param("filter") UserSearchSubentityFilters filter,
            Pageable pageable
    );

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.role.name = :roleName " +
            "AND (:name IS NULL OR LOWER(CONCAT(u.name.firstName, ' ', u.name.lastName)) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<User> findByRoleNameAndNameContaining(@Param("roleName") String roleName, @Param("name") String name, Pageable pageable);


    @Query("SELECT COUNT(u) FROM User u WHERE u.organizationEntity.id = :organizationId AND u.role.name = 'DELIVERY_MAN'")
    Long countDeliveryUsersByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.authorities WHERE u.email = :email")
    Optional<User> findByEmailWithRoleAndAuthorities(@Param("email") String email);

    Page<User> findByOrganizationEntity_Id(UUID organizationId, Pageable pageable);

    Integer countByRoleAndDeletedAtIsNull(Role role);

    Page<User> findByName_FirstNameContainingOrName_LastNameContainingAndRoleNameNot(
            String firstName, String lastName, String roleName, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role " +
            "LEFT JOIN FETCH u.organizationEntity " +
            "LEFT JOIN FETCH u.account " +
            "LEFT JOIN FETCH u.subEntity " +
            "LEFT JOIN FETCH u.address " +
            "LEFT JOIN FETCH u.workingHours " +
            "WHERE u.id = :userId")
    Optional<User> findUserProfileById(@Param("userId") Integer userId);

    @Query("SELECT u FROM User u " +
            "JOIN u.organizationEntity oe " +
            "WHERE (:organizationId IS NULL OR oe.id = :organizationId) " +
            "AND (:name IS NULL OR LOWER(CONCAT(u.name.firstName, ' ', u.name.lastName)) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND u.role.name = :roleName")
    Page<User> getSellsManagers(@Param("organizationId") UUID organizationId, @Param("name") String name, @Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.organizationEntity.id = :organizationId")
    Page<User> findByOrganizationId(
            @Param("organizationId") UUID organizationId,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.subEntity.id = :subEntityId")
    Page<User> findBySubEntityId(@Param("subEntityId") UUID subEntityId, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND (:#{#filter.names} IS NULL OR LOWER(CONCAT(u.name.firstName, ' ', u.name.lastName)) IN :#{#filter.names}) " +
            "AND (:#{#filter.phone} IS NULL OR u.phone = :#{#filter.phone}) " +
            "AND (:#{#filter.city} IS NULL OR u.address.region.city.name = :#{#filter.city}) " +
            "AND (:#{#filter.region} IS NULL OR u.address.region.name = :#{#filter.region}) " +
            "AND (:#{#filter.email} IS NULL OR u.email = :#{#filter.email}) " +
            "AND (:#{#filter.roleName} IS NULL OR u.role.name = :#{#filter.roleName}) " +
            "AND (:#{#filter.entityTypes} IS NULL OR u.organizationEntity.type IN :#{#filter.entityTypes}) " +
            "AND ((:#{#filter.solutions} IS NULL) OR (:#{#filter.solutions} IS NOT NULL AND  (SELECT COUNT(DISTINCT s.name)  FROM Solution s JOIN s.users u2  WHERE u2 = u AND s.name IN (:#{#filter.solutions})) = :#{#filter.solutions.size()}))" +
            "AND (COALESCE(:#{#filter.startDate}, NULL) IS NULL OR u.createdAt >= :#{#filter.startDate}) " +
            "AND (COALESCE(:#{#filter.endDate}, NULL) IS NULL OR u.createdAt <= :#{#filter.endDate}) " +
            "AND (:#{#filter.deletedAt} IS NULL OR (u.deletedAt IS NOT NULL AND :#{#filter.deletedAt} = true) OR (u.deletedAt IS NULL AND :#{#filter.deletedAt} = false))")
    Page<User> findByOrganizationIdWithFilters(
            @Param("organizationId") UUID organizationId,
            @Param("filter") UserFilter filter,
            Pageable pageable
    );

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.subEntity.id = :subEntityId " +
            "AND (:#{#filter.names} IS NULL OR LOWER(CONCAT(u.name.firstName, ' ', u.name.lastName)) IN :#{#filter.names}) " +
            "AND (:#{#filter.phone} IS NULL OR u.phone = :#{#filter.phone}) " +
            "AND (:#{#filter.city} IS NULL OR u.address.region.city.name = :#{#filter.city}) " +
            "AND (:#{#filter.region} IS NULL OR u.address.region.name = :#{#filter.region}) " +
            "AND (:#{#filter.email} IS NULL OR u.email = :#{#filter.email}) " +
            "AND (:#{#filter.roleName} IS NULL OR u.role.name = :#{#filter.roleName}) " +
            "AND (:#{#filter.entityTypes} IS NULL OR u.subEntity.type IN :#{#filter.subEntityTypes}) " +
            "AND (:#{#filter.solutions} IS NULL OR  (SELECT COUNT(DISTINCT s.name)  FROM Solution s JOIN s.users u2  WHERE u2 = u AND s.name IN (:#{#filter.solutions})) = :#{#filter.solutions.size()})" +
            "AND (COALESCE(:#{#filter.startDate}, NULL) IS NULL OR u.createdAt >= :#{#filter.startDate}) " +
            "AND (COALESCE(:#{#filter.endDate}, NULL) IS NULL OR u.createdAt <= :#{#filter.endDate}) " +
            "AND (:#{#filter.deletedAt} IS NULL OR (u.deletedAt IS NOT NULL AND :#{#filter.deletedAt} = true) OR (u.deletedAt IS NULL AND :#{#filter.deletedAt} = false))")
    Page<User> findBySubEntityIdWithFilters(
            @Param("subEntityId") UUID subEntityId,
            @Param("filter") UserFilter filter,
            Pageable pageable
    );

    @Query("SELECT DISTINCT u.address.region.city FROM User u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND LOWER(u.address.region.city.name) LIKE LOWER(CONCAT('%', :cityName, '%')) ")
    Page<City> findCitiesUsersByOrganizationIdAndCityName(@Param("organizationId") UUID organizationId, @Param("cityName") String cityName, Pageable pageable);

    @Query("SELECT DISTINCT u.address.region FROM User u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND LOWER(u.address.region.name) LIKE LOWER(CONCAT('%', :regionName, '%')) ")
    Page<Region> findRegionsUsersByOrganizationIdAndRegionName(@Param("organizationId") UUID organizationId, @Param("regionName") String regionName, Pageable pageable);

    @Query("SELECT DISTINCT u.address.region.city FROM User u " +
            "WHERE u.subEntity.id = :subentityId " +
            "AND LOWER(u.address.region.city.name) LIKE LOWER(CONCAT('%', :cityName, '%')) ")
    Page<City> findCitiesUsersBySubentityIdAndCityName(@Param("subentityId") UUID subentityId, @Param("cityName") String cityName, Pageable pageable);

    @Query("SELECT DISTINCT u.address.region FROM User u " +
            "WHERE u.subEntity.id = :subentityId " +
            "AND LOWER(u.address.region.name) LIKE LOWER(CONCAT('%', :regionName, '%')) ")
    Page<Region> findRegionsUsersBySubentityIdAndRegionName(@Param("subentityId") UUID subentityId, @Param("regionName") String regionName, Pageable pageable);
}
