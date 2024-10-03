package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(@NotBlank String city, @NotBlank String address, @NotBlank String region) {
}
