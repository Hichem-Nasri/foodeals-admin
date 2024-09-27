package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

public interface ReceiverInfo {
    UUID getId();

    DonationReceiverType getReceiverType();
}
