package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.valueObjects.Name;

import java.util.UUID;

public record UserInfoDto(String createdAt, Integer id, String role, Name name, String city, String region, String avatarPath, String email,
                          String phone) {
}
