package net.foodeals.location.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.common.valueOjects.Coordinates;
import net.foodeals.processors.annotations.Processable;

import java.util.UUID;

public record AddressRequest(
        @Processable String country,
        String address,
        @Processable String stateName,
        @Processable String cityName,
         @Processable String regionName,
         String iframe
) {
}
