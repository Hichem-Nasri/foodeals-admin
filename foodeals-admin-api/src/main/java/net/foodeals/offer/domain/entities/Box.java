package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.offer.domain.enums.BoxType;
import net.foodeals.offer.domain.enums.OfferType;

import java.util.List;

@Entity
@Table(name = "boxes")

@Getter
@Setter
public class Box extends AbstractEntity<Long> implements OfferChoice {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoxType type;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BoxItem> boxItems;

    @Override
    public OfferType getOfferType() {
        return OfferType.BOX;
    }
}
