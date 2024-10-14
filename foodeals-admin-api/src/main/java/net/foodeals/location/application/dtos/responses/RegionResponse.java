package net.foodeals.location.application.dtos.responses;

import java.util.UUID;

public record RegionResponse(
        UUID id,
        String name
) {
}
