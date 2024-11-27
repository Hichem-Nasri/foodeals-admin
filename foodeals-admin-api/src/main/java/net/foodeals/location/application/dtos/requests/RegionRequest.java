package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import net.foodeals.processors.annotations.Processable;

public record RegionRequest(
        @NotBlank @Processable String country,
        @NotBlank @Processable String state,
        @NotBlank @Processable String city,
        @NotBlank @Processable String name
) {
}
