package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RegionRequest(@NotBlank String country, @NotBlank String city,
                            @NotBlank String name) {
}
