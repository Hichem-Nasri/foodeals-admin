package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.processors.annotations.Processable;

import java.util.List;

import java.util.List;

public record CreateAssociationDto(
        @NotBlank String companyName,
        @NotNull @Processable List<String> activities,
        @NotNull ContactDto responsible,
        @NotNull Integer managerID,
        @NotNull EntityAddressDto associationAddress,
        @NotNull EntityType entityType,
        Integer numberOfPoints,
        @NotNull @Processable List<String> solutions,
        @NotBlank String pv) {
}