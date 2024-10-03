package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record EventRequest(@NotBlank String object, @NotBlank String message, Integer lead, @NotBlank String dateAndTime) {
}
