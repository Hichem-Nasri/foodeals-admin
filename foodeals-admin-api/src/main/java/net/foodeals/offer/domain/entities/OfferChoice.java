package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.OfferType;

import java.util.UUID;

public interface OfferChoice {

    UUID getId();

    OfferType getOfferType();
}
