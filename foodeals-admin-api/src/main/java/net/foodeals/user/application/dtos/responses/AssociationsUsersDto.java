package net.foodeals.user.application.dtos.responses;


import java.util.UUID;

public record AssociationsUsersDto(Integer id, String city, String region, UserInfoDto userInfoDto) {
}
