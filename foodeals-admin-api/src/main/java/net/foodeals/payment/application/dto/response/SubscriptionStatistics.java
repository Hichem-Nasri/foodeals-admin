package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;

public record SubscriptionStatistics(Price total, Price deadlines) {
}
