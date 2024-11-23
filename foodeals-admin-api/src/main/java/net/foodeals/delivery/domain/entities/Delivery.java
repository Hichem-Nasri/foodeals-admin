package net.foodeals.delivery.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "deliveries")

@Getter
@Setter
public class Delivery extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User deliveryBoy;

    private Integer rating;

    @Enumerated
    private DeliveryStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryPosition deliveryPosition;

    @Builder.Default
    @OneToMany(mappedBy = "delivery", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    public Delivery() {

    }

    public Delivery(User deliveryBoy, DeliveryStatus status) {
        this.deliveryBoy = deliveryBoy;
        this.status = status;
    }

    public static Delivery create(User deliveryBoy, DeliveryStatus status) {
        return new Delivery(deliveryBoy, status);
    }
}
