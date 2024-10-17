package net.foodeals.user.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.processors.annotations.Processable;

import java.util.List;
import java.util.UUID;


public record RoleRequest(
        @NotBlank @Processable String name,
        @NotNull List<UUID> authorityIds
) {
}
