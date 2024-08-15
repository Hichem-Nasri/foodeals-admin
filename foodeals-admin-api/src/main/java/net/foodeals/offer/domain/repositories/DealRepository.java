package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {
}
