package net.foodeals.user.application.dtos.responses;

import java.util.List;

public record DeliveryPartnerUserDto(String createdAt, String role,
                                     String status, String city, String region,
                                     List<String> solutions, UserInfoDto userInfoDto){
}
