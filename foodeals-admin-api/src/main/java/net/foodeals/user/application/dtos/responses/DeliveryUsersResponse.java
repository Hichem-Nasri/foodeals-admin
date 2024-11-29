package net.foodeals.user.application.dtos.responses;

import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import org.springframework.data.domain.Page;

public record DeliveryUsersResponse(PartnerInfoDto organization, Page<DeliveryPartnerUserDto> users) {
}
