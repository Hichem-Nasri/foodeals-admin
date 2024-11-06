package net.foodeals.payment.application.dto.response;

import java.util.List;

public record SubscriptionDetails(PartnerInfoDto partner, List<SubscriptionsDto> subscriptions) {
}
