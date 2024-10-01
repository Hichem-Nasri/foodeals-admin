package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;

import java.util.List;
import java.util.UUID;

public record ProspectRequest(@NotBlank  String companyName, @NotNull List<String> activities, @NotNull ContactDto responsible, @NotNull Integer powered_by, @NotNull Integer manager_id, @NotNull AddressDto address, ProspectStatus status) {}
