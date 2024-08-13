package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.common.valueOjects.Price;

@Entity
@Table(name = "offers")

@Getter
@Setter
public class Offer extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private OfferType type;

    private Price price;

    private Price salePrice;

    private Integer reduction;

    private String barcode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private OpenTime openTime;
}
