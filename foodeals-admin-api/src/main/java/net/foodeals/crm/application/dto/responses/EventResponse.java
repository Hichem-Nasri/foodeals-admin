package net.foodeals.crm.application.dto.responses;

import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.user.application.dtos.responses.UserInfoDto;

import java.util.UUID;

public record EventResponse(UUID id, String createdAt, LeadDto lead, String dateAndTime, String object, String message) {
}
