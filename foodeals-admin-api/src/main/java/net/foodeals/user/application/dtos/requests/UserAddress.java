package net.foodeals.user.application.dtos.requests;

import net.foodeals.processors.annotations.Processable;

public record UserAddress(
        @Processable String country,
        @Processable String city,
        @Processable String region
) {
}
