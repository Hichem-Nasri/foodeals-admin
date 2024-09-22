package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.valueObjects.Name;

public record UserInfoDto(Name name, String avatarPath, String email, String phone) {
}
