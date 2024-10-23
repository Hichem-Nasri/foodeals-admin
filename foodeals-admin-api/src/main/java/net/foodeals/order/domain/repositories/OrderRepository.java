package net.foodeals.order.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.order.domain.entities.Order;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order, UUID> {
    @EntityGraph(attributePaths = "offer.publisherInfo")
    List<Order> findByOfferPublisherInfoId(UUID publisherId);}
