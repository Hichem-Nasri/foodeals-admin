package net.foodeals.product.application.dtos.responses;

import net.foodeals.organizationEntity.domain.entities.Activity;

import java.util.UUID;

public record ProductCategoryResponse(
        UUID id,
        String name,
        String slug,
        Activity activity
) {
}
