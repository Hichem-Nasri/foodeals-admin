package net.foodeals.organizationEntity.application.dtos.requests;

import net.foodeals.processors.annotations.Processable;

public record FeatureRequest(
        @Processable String name) {
}
