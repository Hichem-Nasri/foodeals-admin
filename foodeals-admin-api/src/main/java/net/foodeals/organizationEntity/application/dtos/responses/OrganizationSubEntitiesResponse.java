package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import org.springframework.data.domain.Page;

public record OrganizationSubEntitiesResponse(PartnerInfoDto partnerInfoDto, Page<Object> subentities) {
}
