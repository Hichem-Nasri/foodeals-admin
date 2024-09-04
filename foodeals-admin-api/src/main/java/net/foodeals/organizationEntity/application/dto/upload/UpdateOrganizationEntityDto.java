package net.foodeals.organizationEntity.application.dto.upload;

import lombok.Data;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateOrganizationEntityDto {
    private String entityName; // valid

    private List<String> features;

    private EntityAddressDto entityAddressDto; // valid

    private List<String> solutions;

    private String commercialNumber;

    private UUID salesManageId; // valid

    private EntityContactDto entityContactDto; // valid

    private List<String> activities; // valid

    private Integer maxNumberOfSubEntities; // valid

    private Integer maxNumberOfAccounts; // valid

    private Float minimumReduction; //

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private Boolean oneSubscription; // valid

    private EntityBankInformationDto entityBankInformationDto; // valid
}
