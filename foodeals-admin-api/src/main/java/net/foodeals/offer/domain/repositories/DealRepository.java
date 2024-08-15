package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {
}
