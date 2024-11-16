package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.valueObjects.Name;

import java.util.UUID;

public record UserInfoDto(String createdAt, Integer id, Name name, String avatarPath, String email, String phone) {
}
