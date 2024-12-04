package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.requests.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.processors.annotations.Processable;

import java.util.List;

@Data
public class DeliveryFormData {
    private EntityType entityType;

    private String entityName;

    private EntityFormDataAddress entityAddressDto; // valid

    private List<String> solutions;

    private String commercialNumber;

    private ContactDto contactDto; // valid

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private EntityBankInformationDto entityBankInformationDto;

    private List<CoveredZonesDto> coveredZonesDtos;

    private ContractStatus status;

    private List<String> activities;
}
