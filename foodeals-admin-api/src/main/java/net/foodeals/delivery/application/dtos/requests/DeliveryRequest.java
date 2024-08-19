package net.foodeals.delivery.application.dtos.requests;

import java.util.List;
import java.util.UUID;

import net.foodeals.delivery.domain.enums.DeliveryStatus;

/**
 * DeliveryRequest
 */
public record DeliveryRequest(
        Integer deliveryBoId,
        DeliveryStatus status,
        List<UUID> orderIds,
        List<DeliveryPositionRequest> deliveryPositions) {
}
