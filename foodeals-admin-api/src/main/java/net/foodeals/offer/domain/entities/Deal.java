package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.product.domain.entities.Product;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "deals")

@Getter
@Setter
public class Deal extends AbstractEntity<UUID> implements OfferChoice {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private Price price;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Product product;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Offer> offers;

    @Override
    public OfferType getOfferType() {
        return OfferType.DEAL;
    }
}
