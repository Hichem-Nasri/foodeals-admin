package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.OfferType;

public interface IOfferable {

    Long getId();

    OfferType getOfferType();
}
