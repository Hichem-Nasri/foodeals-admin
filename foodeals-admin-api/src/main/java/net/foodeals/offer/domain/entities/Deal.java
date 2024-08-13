package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.product.domain.entities.Product;

@Entity
@Table(name = "deals")

@Getter
@Setter
public class Deal extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Price price;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Product product;
}
