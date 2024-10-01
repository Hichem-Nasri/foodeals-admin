package net.foodeals.contract.application.DTo.upload;

import lombok.Data;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityBankInformationDto;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;

import java.util.List;

@Data
public class UpdateContractDto {
    private String entityName; // valid

    private EntityAddressDto entityAddressDto; // valid

    private List<String> solutions;

    private String commercialNumber; // valid

    private ContactDto contactDto; // valid

    private List<String> activities; // valid

    private Integer maxNumberOfSubEntities; // valid

    private Integer maxNumberOfAccounts; // valid

    private Float minimumReduction; //

    private List<SolutionsContractDto> solutionsContractDto; // valid

    private Boolean oneSubscription; // valid

    private EntityBankInformationDto entityBankInformationDto;
}
