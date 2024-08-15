package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {
}
