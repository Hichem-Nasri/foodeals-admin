package net.foodeals.location.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.common.valueOjects.Coordinates;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address")

@Getter
@Setter
public class Address extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String address;

    @Column(name = "extra_address")
    private String extraAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private City city;

    private String zip;

    @Embedded
    private Coordinates coordinates;

    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrganizationEntity> organizationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SubEntity> subEntities = new ArrayList<>();
}