package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;

import java.util.List;

@Data
public class UpdateOrganizationEntityDto {
    private String entityName; // valid

    private List<String> features;

    private EntityAddressDto entityAddressDto; // valid

    private List<String> solutions;

    private String commercialNumber;

    private Integer managerId; // valid

    private ContactDto contactDto; // valid

    private List<String> activities; // valid

    private Integer maxNumberOfSubEntities; // valid

    private Integer maxNumberOfAccounts; // valid

    private Float minimumReduction; //

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private Boolean oneSubscription; // valid

    private EntityBankInformationDto entityBankInformationDto; // valid
}
