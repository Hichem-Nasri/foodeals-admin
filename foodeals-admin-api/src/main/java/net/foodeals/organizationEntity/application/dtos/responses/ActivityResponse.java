package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.organizationEntity.domain.entities.enums.ActivityType;

import java.util.UUID;

public record ActivityResponse(UUID id, String name, ActivityType type) {
}
