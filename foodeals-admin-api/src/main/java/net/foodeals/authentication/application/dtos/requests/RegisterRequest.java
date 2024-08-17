package net.foodeals.authentication.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.user.domain.valueObjects.Name;

/**
 * RegisterRequest
 */
public record RegisterRequest(
        @NotNull Name name,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String password,
        Boolean isEmailVerified
) implements AuthRequest {
}
