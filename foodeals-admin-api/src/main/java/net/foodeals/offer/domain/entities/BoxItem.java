package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;

@Entity
@Table(name = "box_items")

@Getter
@Setter
public class BoxItem extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private Price price;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Box box;
}
