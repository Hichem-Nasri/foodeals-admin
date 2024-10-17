package net.foodeals.organizationEntity.application.dtos.requests;

import net.foodeals.processors.annotations.Processable;

public record ActivityRequest(@Processable String name) {
}
