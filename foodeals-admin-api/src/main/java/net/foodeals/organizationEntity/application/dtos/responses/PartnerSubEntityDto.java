package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;
import java.util.UUID;

@Data
public class PartnerSubEntityDto {
    private UUID id;

    private Long offers;

    private Long orders;

    private Long users;

    private String reference;

    private String city;

    private List<String> solutions;

    private String createdAt;

    private PartnerInfoDto partnerInfoDto;

    private ContactDto contactDto;
}
