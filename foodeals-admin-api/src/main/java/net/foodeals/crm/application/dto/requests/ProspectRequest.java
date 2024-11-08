package net.foodeals.crm.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.processors.annotations.Processable;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;


public record ProspectRequest(
        @NotBlank String companyName,
        @NotNull @Processable List<String> activities,
        @NotNull ContactDto responsible,
        @NotNull Integer manager_id,
        @NotNull AddressDto address,
        ProspectStatus status,
        @Nullable @Processable List<String> solutions,
        @Nullable EventRequest event,
        ProspectType type
) {
}