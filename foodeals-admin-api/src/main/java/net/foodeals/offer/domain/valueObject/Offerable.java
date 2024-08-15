package net.foodeals.offer.domain.valueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.foodeals.offer.domain.enums.OfferType;

@Embeddable
public record Offerable(
        @Column(name = "offerable_id")
        Long id,

        @Enumerated(EnumType.STRING)
        @Column(name = "offerable_type")
        OfferType type) {
}