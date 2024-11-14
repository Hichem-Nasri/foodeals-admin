package net.foodeals.user.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.user.application.dtos.responses.UserFilter;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User, Integer> {


    @Query("SELECT u FROM User u " +
            "JOIN u.organizationEntity oe " +
            "WHERE (:#{#filter.query} IS NULL OR CONCAT(u.name.firstName, ' ', u.name.lastName) LIKE CONCAT('%', :#{#filter.query}, '%')) " +
            "AND (:#{#filter.types} IS NULL OR oe.type IN :#{#filter.types}) ")
    Page<User> findWithFilters(
            @Param("filter") UserFilter filter,
            Pageable pageable
    );

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

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
}
