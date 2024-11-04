package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.util.List;
import java.util.UUID;

public record SubscriptionsListDto(UUID reference, PartnerInfoDto partner, PartnerType type, Price total, List<String> solutions, List<SubscriptionsDto> subscriptions, Boolean payable) {
}
