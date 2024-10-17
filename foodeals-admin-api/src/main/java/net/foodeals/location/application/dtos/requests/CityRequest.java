package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.processors.annotations.Processable;

import java.util.UUID;

public record CityRequest(
        @NotBlank @Processable String country,
        @NotBlank @Processable String name
) {
}
