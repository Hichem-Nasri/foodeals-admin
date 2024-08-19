package net.foodeals.delivery.application.dtos.requests.delivery;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import net.foodeals.delivery.domain.enums.DeliveryStatus;

/**
 * DeliveryRequest
 */
public record DeliveryRequest(
        @NotNull Integer deliveryBoId,
        @NotNull DeliveryStatus status,
        @NotNull List<UUID> orderIds,
        @NotNull List<DeliveryRequest> deliveryPositions) {
}
