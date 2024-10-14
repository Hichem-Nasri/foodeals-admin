package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.common.valueOjects.Coordinates;

import java.util.UUID;

public record AddressRequest(
        @NotBlank String country,
        @NotBlank String address,
        @NotNull String cityName,
        @NotNull String regionName,
        @NotNull String iframe
        ) {
}
