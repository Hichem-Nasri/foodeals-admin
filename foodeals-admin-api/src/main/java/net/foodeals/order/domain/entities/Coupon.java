package net.foodeals.order.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;

import java.util.List;

@Entity
@Table(name = "coupons")

@Getter
@Setter
public class Coupon extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private Float discount;

    @ManyToOne(cascade = CascadeType.ALL)
    private SubEntity subEntity;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Order> orders;
}
