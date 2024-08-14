package net.foodeals.delivery.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.user.domain.entities.User;

import java.util.List;

@Entity
@Table(name = "deliveries")

@Getter
@Setter
public class Delivery extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User deliveryBoy;

    private Integer rating;

    @Enumerated
    private DeliveryStatus status;

    @OneToMany(mappedBy = "delivery", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPosition> deliveryPositions;
}
