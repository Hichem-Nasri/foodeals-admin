package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.PublisherType;

import java.util.UUID;

public interface PublisherI {
    UUID getId();

    PublisherType getPublisherType();
}