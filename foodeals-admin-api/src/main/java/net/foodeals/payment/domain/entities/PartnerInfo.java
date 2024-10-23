package net.foodeals.payment.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

@Embeddable
public record PartnerInfo(@Column(name = "partner_Id")
                          UUID id,
                          @Enumerated(EnumType.STRING)
                          @Column(name = "partner_type")
                          PartnerType type) {}
