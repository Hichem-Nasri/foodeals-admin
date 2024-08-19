package net.foodeals.delivery.application.dtos.requests;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import net.foodeals.common.valueOjects.Coordinates;

/**
 * DeliveryPositionRequest
 */
public record DeliveryPositionRequest(
        @NotNull Coordinates coordinates,
        @NotNull UUID deliveryId 
        ) {
}
