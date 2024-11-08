package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;
import java.util.UUID;

public record AssociationsDto(UUID id, String createdAt, PartnerInfoDto partner, ResponsibleInfoDto responsible,
                              Integer users, Integer donations, Integer recovered, Integer subEntities, Integer associations,
                              ContractStatus status, String city, List<String> solutions, EntityType type) {
}
