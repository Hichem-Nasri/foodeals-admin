package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
