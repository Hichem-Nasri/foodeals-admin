package net.foodeals.delivery.domain.repositories;

import net.foodeals.delivery.domain.entities.DeliveryPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPositionRepository extends JpaRepository<DeliveryPosition, Long> {
}
