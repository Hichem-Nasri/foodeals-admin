package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;
import java.util.UUID;

public record AssociationsSubEntitiesDto(UUID id, String createdAt, PartnerInfoDto partnerInfoDto, ResponsibleInfoDto responsibleInfoDto,
                                         Integer users, Integer donations, Integer recovered,
                                         String city, List<String> solutions) {
}

