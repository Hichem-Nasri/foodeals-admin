package net.foodeals.delivery.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

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
}
