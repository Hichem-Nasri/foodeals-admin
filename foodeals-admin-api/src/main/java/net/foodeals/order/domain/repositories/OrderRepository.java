package net.foodeals.order.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.enums.OrderStatus;
import net.foodeals.order.domain.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT o FROM Order o WHERE o.offer.publisherInfo.id = :publisherId " +
            "AND EXTRACT(MONTH FROM o.createdAt) = EXTRACT(MONTH FROM CAST(:orderDate AS timestamp)) " +
            "AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CAST(:orderDate AS timestamp))")
    Page<Order> findOrdersByPublisherIdAndOrderDate(@Param("publisherId") UUID publisherId, @Param("orderDate") Date orderDate, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.transaction t WHERE o.offer.publisherInfo.id = :publisherId " +
            "AND EXTRACT(MONTH FROM o.createdAt) = EXTRACT(MONTH FROM CAST(:orderDate AS timestamp)) " +
            "AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CAST(:orderDate AS timestamp)) " +
            "AND o.status = :orderStatus " +
            "AND t.status = :transactionStatus")
    Page<Order> findOrdersByPublisherIdAndOrderDateAndStatusAndTransactionStatus(
            @Param("publisherId") UUID publisherId,
            @Param("orderDate") Date orderDate,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("transactionStatus") TransactionStatus transactionStatus,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o JOIN o.transaction t WHERE o.offer.publisherInfo.id = :publisherId " +
            "AND EXTRACT(MONTH FROM o.createdAt) = EXTRACT(MONTH FROM CAST(:orderDate AS timestamp)) " +
            "AND EXTRACT(YEAR FROM o.createdAt) = EXTRACT(YEAR FROM CAST(:orderDate AS timestamp)) " +
            "AND o.status = :orderStatus " +
            "AND t.status = :transactionStatus")
    List<Order> findOrdersByPublisherIdAndOrderDateAndStatusAndTransactionStatus(
            @Param("publisherId") UUID publisherId,
            @Param("orderDate") Date orderDate,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("transactionStatus") TransactionStatus transactionStatus
    );

    @Query("SELECT o FROM Order o " +
            "JOIN o.delivery d " +
            "JOIN d.deliveryBoy u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND d.status = :deliveryStatus "  +
            "AND DATE(o.createdAt) = :orderDate " +
            "AND o.status = :orderStatus " +
            "AND o.transaction.status = :transactionStatus")
    Page<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(
            @Param("organizationId") UUID organizationId,
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("orderDate") Date orderDate,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("transactionStatus") TransactionStatus transactionStatus,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o " +
            "JOIN o.delivery d " +
            "JOIN d.deliveryBoy u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND d.status = :deliveryStatus "  +
            "AND DATE(o.createdAt) = :orderDate " +
            "AND o.status = :orderStatus " +
            "AND o.transaction.status = :transactionStatus")
    List<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(
            @Param("organizationId") UUID organizationId,
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("orderDate") Date orderDate,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("transactionStatus") TransactionStatus transactionStatus
    );

    @Query("SELECT COUNT(o) FROM Order o " +
            "JOIN o.delivery d " +
            "JOIN d.deliveryBoy u " +
            "WHERE u.organizationEntity.id = :organizationId " +
            "AND d.status = :deliveryStatus "  +
            "AND DATE(o.createdAt) = :orderDate " +
            "AND o.status = :orderStatus " +
            "AND o.transaction.status = :transactionStatus")
    Long countOrdersByOrganizationAndDeliveryStatusAndCriteria(
            @Param("organizationId") UUID organizationId,
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("orderDate") Date orderDate,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("transactionStatus") TransactionStatus transactionStatus
    );
}



