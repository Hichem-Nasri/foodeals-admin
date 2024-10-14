package net.foodeals.location.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Coordinates;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "address")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String address;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne
    private Region region;

    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToOne(mappedBy = "address")
    private OrganizationEntity organizationEntity;

    @OneToOne(mappedBy = "address")
    private SubEntity subEntity;

    @Column(length = 2000)
    private String iframe;
}