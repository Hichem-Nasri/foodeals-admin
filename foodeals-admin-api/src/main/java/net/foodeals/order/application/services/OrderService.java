package net.foodeals.order.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.order.application.dtos.requests.OrderRequest;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.enums.OrderStatus;
import net.foodeals.order.domain.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderService extends CrudService<Order, UUID, OrderRequest> {
    List<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date);
    Page<Order> findByOfferPublisherInfoIdAndDateAndStatus(UUID publisherId, Date date, OrderStatus status, TransactionStatus transactionStatus, Pageable pageable);
    Page<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date, Pageable pageable);
    List<Order> findByOfferPublisherInfoIdAndDateAndStatus(UUID publisherId, Date date, OrderStatus status, TransactionStatus transactionStatus);
    Page<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(
            UUID organizationId,
            DeliveryStatus deliveryStatus,
            Date orderDate,
            OrderStatus orderStatus,
            TransactionStatus transactionStatus,
            Pageable pageable
    );

        List<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(
            UUID organizationId,
            DeliveryStatus deliveryStatus,
            Date orderDate,
            OrderStatus orderStatus,
            TransactionStatus transactionStatus
    );
}
