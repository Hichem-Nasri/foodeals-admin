package net.foodeals.crm.application.dto.responses;

import net.foodeals.user.domain.valueObjects.Name;

public record CreatorInfoDto(Name name, String avatarPath) {
}
