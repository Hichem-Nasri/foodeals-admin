package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;

public record AssociationsSubEntitiesDto(String createdAt, PartnerInfoDto partnerInfoDto, ResponsibleInfoDto responsibleInfoDto,
                                         Integer users, Integer donations, Integer recovered,
                                         String city, List<String> solutions, SubEntityType type) {
}

