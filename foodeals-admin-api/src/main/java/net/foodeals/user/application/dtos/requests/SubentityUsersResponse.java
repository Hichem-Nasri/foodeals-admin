package net.foodeals.user.application.dtos.requests;

import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.application.dtos.responses.UserInfoDto;
import org.springframework.data.domain.Page;

public record SubentityUsersResponse(PartnerInfoDto organization, Page<UserInfoDto> users) {
}
