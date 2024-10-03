package net.foodeals.crm.application.dto.requests;

import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import org.springframework.lang.Nullable;

import java.util.List;


public record PartialProspectRequest(
        @Nullable String companyName,
        @Nullable List<String> activities,
        @Nullable ContactDto responsible,
        @Nullable Integer powered_by,
        @Nullable Integer manager_id,
        @Nullable AddressDto address,
        @Nullable ProspectStatus status,
        @Nullable List<String> solutions
        ) {
}