package net.foodeals.contract.application.DTo.upload;

import lombok.Data;

@Data
public class SolutionsContractDto {

    private String solution;

    private ContractSubscriptionDto contractSubscriptionDto;

    private ContractCommissionDto contractCommissionDto;
}