package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.valueObjects.Name;

import java.util.UUID;

public record UserResponse(
        Integer id,
        Name name,
        String email,
        String phone,
        RoleResponse role,
        UUID organizationEntityId
) {
}
