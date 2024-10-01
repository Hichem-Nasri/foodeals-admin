package net.foodeals.offer.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.foodeals.offer.domain.enums.DonorType;
import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

@Embeddable
public record Donor(@Column(name = "donor_id")
                            UUID id,
                    @Enumerated(EnumType.STRING)
                    @Column(name = "donor_type")
                    DonorType type) {}