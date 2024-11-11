package net.foodeals.organizationEntity.application.dtos.requests;

import net.foodeals.organizationEntity.domain.entities.enums.ActivityType;
import net.foodeals.processors.annotations.Processable;

public record ActivityRequest(@Processable String name, @Processable ActivityType type) {
}
