package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.OfferType;

public interface OfferChoice {

    Long getId();

    OfferType getOfferType();
}
