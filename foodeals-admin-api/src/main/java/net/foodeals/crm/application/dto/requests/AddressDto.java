package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record AddressDto(@NotBlank String country, @NotBlank String city, @NotBlank String address, @NotBlank String region, @Nullable String iframe) {
}
