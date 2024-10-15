package net.foodeals.offer.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.foodeals.offer.domain.enums.PublisherType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

public record PublisherInfo(@Column(name = "publisher_id")
                            UUID id,
                            @Enumerated(EnumType.STRING)
                            @Column(name = "publisherType")
                            PublisherType type) {}