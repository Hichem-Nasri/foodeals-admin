package net.foodeals.order.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.offer.application.services.OfferService;
import net.foodeals.offer.domain.entities.Offer;
import net.foodeals.offer.domain.entities.PublisherInfo;
import net.foodeals.offer.domain.enums.PublisherType;
import net.foodeals.order.application.dtos.requests.OrderRequest;
import net.foodeals.order.application.services.CouponService;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.enums.*;
import net.foodeals.order.domain.exceptions.OrderNotFoundException;
import net.foodeals.order.domain.repositories.OrderRepository;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.payment.application.dto.response.OperationsDto;
import net.foodeals.payment.application.dto.response.ProductInfo;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final CouponService couponService;
    private final AddressService addressService;
    private final UserService userService;
    private final OfferService offerService;
    private final DeliveryService deliveryService;
    private final SubEntityService subEntityService;
    private final OrganizationEntityService organizationEntityService;
    private final CommissionService commissionService;

    @Override
    public List<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date) {
        return this.repository.findOrdersByPublisherIdAndOrderDate(publisherId, date);
    }

    @Override
    public Page<Order> findByOfferPublisherInfoIdAndDateAndStatus(UUID publisherId, Date date, OrderStatus status, TransactionStatus transactionStatus, Pageable pageable) {
        return this.repository.findOrdersByPublisherIdAndOrderDateAndStatusAndTransactionStatus(publisherId, date, status, transactionStatus, pageable);
    }

    @Override
    public Page<Order> findByOfferPublisherInfoIdAndDate(UUID publisherId, Date date, Pageable pageable) {
        return this.repository.findOrdersByPublisherIdAndOrderDate(publisherId, date, pageable);
    }

    @Override
    public List<Order> findByOfferPublisherInfoIdAndDateAndStatus(UUID publisherId, Date date, OrderStatus status, TransactionStatus transactionStatus) {
        return this.repository.findOrdersByPublisherIdAndOrderDateAndStatusAndTransactionStatus(publisherId, date, status, transactionStatus);
    }

    @Override
    @Transactional
    public Page<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(UUID organizationId, DeliveryStatus deliveryStatus, Date orderDate, OrderStatus orderStatus, TransactionStatus transactionStatus, Pageable pageable) {
        return this.repository.findOrdersByOrganizationAndDeliveryStatusAndCriteria(organizationId, deliveryStatus, orderDate, orderStatus, transactionStatus, pageable);
    }

    @Override
    @Transactional
    public List<Order> findOrdersByOrganizationAndDeliveryStatusAndCriteria(UUID organizationId, DeliveryStatus deliveryStatus, Date orderDate, OrderStatus orderStatus, TransactionStatus transactionStatus) {
        return this.repository.findOrdersByOrganizationAndDeliveryStatusAndCriteria(organizationId, deliveryStatus, orderDate, orderStatus, transactionStatus);
    }

    @Override
    public Page<OperationsDto> getOperationsByOrderId(UUID orderId, Pageable pageable) {
        Order order = this.repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        List<Order> orders = this.repository.findOrdersByDeliveryId(order.getDelivery().getId());
        List<OperationsDto> operationsDtos = new ArrayList<>();
        UUID organizationId = order.getOffer().getPublisherInfo().type().equals(PublisherType.PARTNER_SB) ? this.subEntityService.getEntityById(order.getOffer().getPublisherInfo().id()).getOrganizationEntity().getId() : order.getOffer().getPublisherInfo().id();
        Commission commission = this.commissionService.getCommissionByPartnerId(organizationId);
        for (Order sameDeliveryOrder : orders) {
            Transaction transaction = sameDeliveryOrder.getTransaction();
            boolean isCash = transaction.getType().equals(TransactionType.CASH);
            Price amount = transaction.getPrice();
            Price cashAmount = isCash ? amount : Price.ZERO(Currency.getInstance("MAD"));
            Price cardAmount = isCash ? Price.ZERO(Currency.getInstance("MAD")) : amount;
            BigDecimal commissionRate = BigDecimal.valueOf(isCash ? commission.getCash() : commission.getCard()).divide(BigDecimal.valueOf(100));
            Price commissionAmount = new Price(commissionRate.multiply(amount.amount()), Currency.getInstance("MAD"));
            Price cashCommission = isCash ? commissionAmount : Price.ZERO(Currency.getInstance("MAD"));
            Price cardCommission = isCash ? Price.ZERO(Currency.getInstance("MAD")) : commissionAmount;
            ProductInfo productInfo = new ProductInfo(sameDeliveryOrder.getOffer().getTitle(), sameDeliveryOrder.getOffer().getImagePath());
            OperationsDto operationsDto = new OperationsDto(
                    OrderDeliveryType.SINGLE,
                    productInfo,
                    sameDeliveryOrder.getId(),
                    sameDeliveryOrder.getOffer().getSalePrice(),
                    sameDeliveryOrder.getQuantity(),
                    cashAmount,
                    cashCommission,
                    cardAmount,
                    cardCommission
            );
            operationsDtos.add(operationsDto);
        }
        return new PageImpl<>(operationsDtos, pageable, operationsDtos.size());
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Order> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Order findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public Order create(OrderRequest request) {
        final User client = userService.findById(request.clientId());
        final Offer offer = offerService.findById(request.offerId());

        final Order order = Order.create(request.price(), request.type(), request.status(), client, offer);

        if (request.type().equals(OrderType.DELIVERY)) {
            order
                    .setShippingAddress(addressService.create(request.shippingAddress()))
                    .setDelivery(deliveryService.create(request.delivery()));
        }

        if (request.couponId() != null) {
            order.setCoupon(
                    couponService.findById(request.couponId())
            );
        }

        return repository.save(order);
    }

    @Override
    public Order update(UUID id, OrderRequest request) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        if (repository.existsById(id))
            throw new OrderNotFoundException(id);

        repository.softDelete(id);
    }
}
