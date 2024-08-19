package net.foodeals.delivery.application.dtos.requests;

import net.foodeals.common.valueOjects.Coordinates;

/**
 * DeliveryPositionRequest
 */
public record DeliveryPositionRequest(
        Coordinates coordinates) {
}
