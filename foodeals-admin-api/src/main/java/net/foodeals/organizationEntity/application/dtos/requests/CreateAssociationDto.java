package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.processors.annotations.Processable;

import java.util.List;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssociationDto {
    @NotBlank
    private String companyName;
    @NotNull
    @Processable
    private List<String> activities;
    @NotNull
    private ContactDto responsible;
    @NotNull
    private Integer managerID;
    @NotNull
    private EntityAddressDto associationAddress;
    @NotNull
    private EntityType entityType;
    private Integer numberOfPoints;
    @NotNull
    @Processable
    private List<String> solutions;
    @NotBlank
    private String pv;
}