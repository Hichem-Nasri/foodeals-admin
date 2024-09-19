package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;

@Data
public class EntityBankInformationDto {

    private String beneficiaryName;

    private String bankName;

    private String rib;
}
