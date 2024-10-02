package net.foodeals.crm.application.dto.requests;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record PartialEventRequest(
        @Nullable Integer lead,
        @Nullable String object,
        @Nullable String message,
        @Nullable String dateAndTime
) {
}
