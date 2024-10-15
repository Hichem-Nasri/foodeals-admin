package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;
import java.util.UUID;

@Data
public class OrganizationEntityDto {
    private UUID id;

    private Long offers;

    private Long orders;
    private Long users;

    private Long subEntities;
    private EntityType type;
    private String city;

    private List<String> solutions;

    private String createdAt;

    private PartnerInfoDto partnerInfoDto;

    private ContractStatus contractStatus;

    private ContactDto contactDto;
}
