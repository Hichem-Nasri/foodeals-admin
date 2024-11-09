package net.foodeals.contract.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {
    List<Subscription> findByStartDateBetweenAndSubscriptionStatusNot(LocalDate startDate, LocalDate endDate, SubscriptionStatus status);

    List<Subscription> findByPartner_OrganizationIdAndStartDateBetweenAndSubscriptionStatusNot(UUID organizationId, LocalDate startDate, LocalDate endDate, SubscriptionStatus status);

    List<Subscription> findByPartner_IdAndStartDateBetweenAndSubscriptionStatusNot(UUID id, LocalDate startDate, LocalDate endDate, SubscriptionStatus status);


        @Query("SELECT DISTINCT YEAR(s.startDate) FROM Subscription s WHERE s.startDate IS NOT NULL")
        List<Integer> findAvailableStartYears();

        @Query("SELECT DISTINCT YEAR(s.endDate) FROM Subscription s WHERE s.endDate IS NOT NULL")
        List<Integer> findAvailableEndYears();

        // Optional: Combine both start and end years into a single list
        default List<Integer> findAvailableYears() {
            List<Integer> startYears = findAvailableStartYears();
            List<Integer> endYears = findAvailableEndYears();

            Set<Integer> allYears = new HashSet<>(startYears);
            allYears.addAll(endYears);

            return new ArrayList<>(allYears);
        }
}
// 23 ->
