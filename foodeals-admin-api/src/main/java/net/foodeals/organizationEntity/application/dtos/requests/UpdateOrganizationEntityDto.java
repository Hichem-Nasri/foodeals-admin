package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.responses.DeliveryPartnerDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.processors.annotations.Processable;

import java.util.List;

@Data
public class UpdateOrganizationEntityDto {

    private EntityType entityType;

    private String entityName;

    private Boolean commissionPayedBySubEntities;

    private EntityAddressDto entityAddressDto; // valid

    @Processable
    private List<String> solutions;

    @Processable
    private List<String> features;

    private String commercialNumber;

    private Integer managerId; // valid

    private ContactDto contactDto; // valid

    @Processable
    private List<String> activities;

    private Integer maxNumberOfSubEntities; // valid

    private Integer maxNumberOfAccounts; // valid

    private Float minimumReduction; //

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private Boolean oneSubscription; // valid

    private EntityBankInformationDto entityBankInformationDto;

    private List<CoveredZonesDto> coveredZonesDtos;

    private List<DeliveryPartnerContract> deliveryPartnerContracts;

    private ContractStatus status;

    private boolean subscriptionPayedBySubEntities;
}
