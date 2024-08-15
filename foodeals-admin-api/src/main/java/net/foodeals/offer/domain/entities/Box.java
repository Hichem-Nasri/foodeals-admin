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
public class Box extends AbstractEntity<Long> implements IOfferable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BoxItem> boxItems;

    @Enumerated(EnumType.STRING)
    private BoxType type;

    @Override
    public OfferType getOfferType() {
        return OfferType.BOX;
    }
}
