package net.foodeals.crm.application.dto.responses;

import net.foodeals.user.domain.valueObjects.Name;

public record LeadDto(Name name, String avatarPath) {
}
