package net.foodeals.contract.application.DTo.upload;

import lombok.Data;
import org.hibernate.engine.profile.Association;

@Data
public class ContractCommissionDto {

    private Float withCard;

    private Float withCash;

    private Float deliveryAmount;

    private Float deliveryCommission;
}
