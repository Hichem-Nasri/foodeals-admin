package net.foodeals.order.domain.repositories;

import net.foodeals.order.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
