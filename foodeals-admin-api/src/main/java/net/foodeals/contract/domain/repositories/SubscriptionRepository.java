package net.foodeals.contract.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE YEAR(s.startDate) <= :year AND YEAR(s.endDate) >= :year")
    Page<Subscription> findSubscriptionsByYear(@Param("year") int year, Pageable pageable);
}
