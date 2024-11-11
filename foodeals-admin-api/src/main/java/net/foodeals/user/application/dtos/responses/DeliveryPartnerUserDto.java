package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.entities.UserStatus;

import java.util.List;

public record DeliveryPartnerUserDto(String createdAt, String role,
                                     UserStatus status, String city, String region,
                                     List<String> solutions, UserInfoDto userInfoDto){
}
