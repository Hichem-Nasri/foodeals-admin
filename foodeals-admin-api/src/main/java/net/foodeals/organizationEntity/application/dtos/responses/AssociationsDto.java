package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;

public record AssociationsDto(String createdAt, PartnerInfoDto partnerInfoDto, ResponsibleInfoDto responsibleInfoDto,
                              Integer users, Integer donations, Integer recovered, Integer subEntities, Integer associations,
                              ContractStatus status, String city, List<String> solutions, EntityType type) {
}
