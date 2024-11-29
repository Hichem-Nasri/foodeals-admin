package net.foodeals.payment.domain.entities;

import net.foodeals.offer.domain.enums.OfferType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.UUID;

public interface PartnerI {
    UUID getId();

    PartnerType getPartnerType();

    String getName();

    String getAvatarPath();

    boolean commissionPayedBySubEntities();
    boolean subscriptionPayedBySubEntities();
    boolean singleSubscription();

    String getCity();

}
