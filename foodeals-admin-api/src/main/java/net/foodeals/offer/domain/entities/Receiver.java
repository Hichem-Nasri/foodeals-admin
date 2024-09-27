package net.foodeals.offer.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

@Embeddable
public record Receiver(@Column(name = "receiver_id")
                    UUID id,
                    @Enumerated(EnumType.STRING)
                    @Column(name = "receiver_type")
                       DonationReceiverType type) {}
