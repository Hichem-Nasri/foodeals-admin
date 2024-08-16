package net.foodeals.order.domain.entities;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.offer.domain.entities.Offer;
import net.foodeals.order.domain.enums.OrderStatus;
import net.foodeals.order.domain.enums.OrderType;
import net.foodeals.user.domain.entities.User;

@Entity
@Table(name = "orders")

@Getter
@Setter
public class Order extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Embedded
    private Price price;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address shippingAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Offer offer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Delivery delivery;

    @ManyToOne(cascade = CascadeType.ALL)
    private Coupon coupon;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transcation> transactions;
}
