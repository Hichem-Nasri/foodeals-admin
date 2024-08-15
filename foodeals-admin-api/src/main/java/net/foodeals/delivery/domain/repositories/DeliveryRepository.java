package net.foodeals.delivery.domain.repositories;

import net.foodeals.delivery.domain.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
