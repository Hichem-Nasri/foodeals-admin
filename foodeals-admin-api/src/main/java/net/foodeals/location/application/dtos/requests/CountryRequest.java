package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import net.foodeals.processors.annotations.Processable;

public record CountryRequest(
        @NotBlank @Processable String name
) {
}
