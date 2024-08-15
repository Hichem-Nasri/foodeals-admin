package net.foodeals.delivery.domain.repositories;

import net.foodeals.delivery.domain.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
