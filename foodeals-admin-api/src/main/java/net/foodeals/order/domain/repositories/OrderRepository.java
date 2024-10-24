package net.foodeals.order.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.order.domain.entities.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.offer.publisherInfo.id = :publisherId " +
            "AND EXTRACT(MONTH FROM o.createdAt) = EXTRACT(MONTH FROM CAST(:orderDate AS timestamp)) " +
            "AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CAST(:orderDate AS timestamp))")
    List<Order> findOrdersByPublisherIdAndOrderDate(@Param("publisherId") UUID publisherId, @Param("orderDate") Date orderDate);
}
