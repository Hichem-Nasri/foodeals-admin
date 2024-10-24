package net.foodeals.order.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.order.application.dtos.requests.OrderRequest;
import net.foodeals.order.domain.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderService extends CrudService<Order, UUID, OrderRequest> {
    List<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date);
    Page<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date, Pageable pageable);

}
