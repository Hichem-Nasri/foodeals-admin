package net.foodeals.user.application.dtos.responses;


import java.util.UUID;

public record AssociationsUsersDto(Integer id, String createdAt, String roleName, String city, String region, UserInfoDto userInfoDto) {
}
