package net.foodeals.crm.application.dto.responses;

import net.foodeals.crm.application.dto.requests.AddressDto;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;

import java.util.List;
import java.util.UUID;

public record ProspectResponse(UUID id, String createdAt, String companyName, String category, ContactDto contact, AddressDto address, CreatorInfoDto creatorInfo, ManagerInfoDto managerInfo, ProspectStatus status, List<EventResponse> event, List<String> solutions) {
}
