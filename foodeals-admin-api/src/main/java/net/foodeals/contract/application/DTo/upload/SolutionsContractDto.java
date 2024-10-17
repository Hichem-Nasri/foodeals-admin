package net.foodeals.contract.application.DTo.upload;

import lombok.Data;
import net.foodeals.processors.annotations.Processable;

@Data
public class SolutionsContractDto {

    @Processable
    private String solution;

    private ContractSubscriptionDto contractSubscriptionDto;

    private ContractCommissionDto contractCommissionDto;
}