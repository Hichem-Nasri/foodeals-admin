package net.foodeals.authentication.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * RegisterRequest
 */
public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String password,
        Boolean isEmailVerified
) implements AuthRequest {
}
