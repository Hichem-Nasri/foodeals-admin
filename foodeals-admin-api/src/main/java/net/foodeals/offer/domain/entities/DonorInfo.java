package net.foodeals.offer.domain.entities;

import net.foodeals.offer.domain.enums.DonorType;
import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

public interface DonorInfo {
    UUID getId();

    DonorType getDonorType();
}