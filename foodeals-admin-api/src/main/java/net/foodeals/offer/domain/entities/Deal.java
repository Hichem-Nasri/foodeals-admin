package net.foodeals.offer.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;

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
}
