package net.foodeals.crm.application.dto.responses;

import net.foodeals.user.domain.valueObjects.Name;

public record ManagerInfoDto(Name name, String avatarPath, Integer id) {
}
