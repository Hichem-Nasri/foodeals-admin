package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.common.valueOjects.Coordinates;
import net.foodeals.processors.annotations.Processable;

import java.util.UUID;

public record AddressRequest(
        @NotBlank @Processable String country,
        @NotBlank String address,
        @NotNull @Processable String cityName,
        @NotNull @Processable String regionName,
        @NotNull String iframe
) {
}
