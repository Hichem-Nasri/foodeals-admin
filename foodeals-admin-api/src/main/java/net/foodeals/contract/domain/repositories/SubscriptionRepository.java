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
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {
    List<Subscription> findByStartDateBetweenAndSubscriptionStatusNot(LocalDate startDate, LocalDate endDate, SubscriptionStatus status);

    List<Subscription> findByPartner_OrganizationIdAndStartDateBetweenAndSubscriptionStatusNot(UUID organizationId, LocalDate startDate, LocalDate endDate, SubscriptionStatus status);
}
// 23 ->
