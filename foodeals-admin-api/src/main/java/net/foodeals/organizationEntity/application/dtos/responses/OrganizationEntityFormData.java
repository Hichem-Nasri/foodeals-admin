package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.CoveredZonesDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityBankInformationDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.processors.annotations.Processable;
import net.foodeals.user.application.dtos.responses.UserInfoDto;

import java.util.List;

@Data
public class OrganizationEntityFormData {
    private EntityType entityType;

    private String entityName;

    private Boolean commissionPayedBySubEntities;

    private EntityFormDataAddress entityAddressDto; // valid

    private List<String> solutions;

    private List<String> features;

    private String commercialNumber;

    private UserInfoDto manager; // valid

    private ContactDto contactDto; // valid

    private List<String> activities;

    private Integer maxNumberOfSubEntities; // valid

    private Integer maxNumberOfAccounts; // valid

    private Float minimumReduction; //

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private Boolean oneSubscription; // valid

    private EntityBankInformationDto entityBankInformationDto;

    private ContractStatus status;

    private boolean subscriptionPayedBySubEntities;
}
