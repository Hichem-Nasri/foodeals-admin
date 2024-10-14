package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CityRequest(
        @NotBlank String country,
        @NotBlank String name
) {
}
