package net.foodeals.crm.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.crm.application.dto.responses.ProspectFilter;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
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
            "JOIN c.country co " +
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

    // Count prospects by status and type, excluding deleted ones
    Long countByStatusAndTypeInAndDeletedAtIsNull(ProspectStatus status, List<ProspectType> type);

    // Count prospects by type, excluding deleted ones
    Long countByTypeIn(List<ProspectType> type);

}