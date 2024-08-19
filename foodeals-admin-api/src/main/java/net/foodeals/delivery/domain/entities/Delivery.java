package net.foodeals.delivery.domain.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;

import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.user.domain.entities.User;

@Entity
@Table(name = "deliveries")

@Getter
@Setter
public class Delivery extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User deliveryBoy;

    private Integer rating;

    @Enumerated
    private DeliveryStatus status;

    @OneToMany(mappedBy = "delivery", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPosition> deliveryPositions;

    @OneToMany(mappedBy = "delivery", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
