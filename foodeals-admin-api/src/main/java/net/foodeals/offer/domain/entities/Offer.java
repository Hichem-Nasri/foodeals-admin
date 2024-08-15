package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.offer.domain.valueObject.Offerable;
import net.foodeals.organizationEntity.domain.entities.Activity;

@Entity
@Table(name = "offers")

@Getter
@Setter
public class Offer extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Price price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "sale_price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "sale_price_currency"))
    })
    private Price salePrice;

    private Integer reduction;

    private String barcode;

    @ManyToOne(cascade = CascadeType.ALL)
    private OpenTime openTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private Activity activity;

    @Embedded
    private Offerable offerable;

    @Transient
    private OfferChoice offerChoice;
}
