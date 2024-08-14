package net.foodeals.delivery.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Coordinates;

@Entity
@Table(name = "delivery_positions")

@Getter
@Setter
public class DeliveryPosition extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Delivery delivery;
}
