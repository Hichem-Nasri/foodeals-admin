package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import net.foodeals.processors.annotations.Processable;
import org.springframework.lang.Nullable;

public record AddressDto(
        @NotBlank @Processable String country,
        @NotBlank @Processable String city,
        @NotBlank String address,
        @NotBlank @Processable String region,
        @Nullable String iframe
) {
}
