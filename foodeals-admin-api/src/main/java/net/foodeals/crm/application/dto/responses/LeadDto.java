package net.foodeals.crm.application.dto.responses;

import net.foodeals.user.domain.valueObjects.Name;

import java.util.UUID;

public record LeadDto(Name name, String avatarPath, Integer id) {
}
