package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;

import java.util.List;
import java.util.UUID;

public record SubscriptionsDto(UUID reference, Price total, List<String> solutions, List<DeadlinesDto> deadlines) {
}
// fetch subscriptions by orga
