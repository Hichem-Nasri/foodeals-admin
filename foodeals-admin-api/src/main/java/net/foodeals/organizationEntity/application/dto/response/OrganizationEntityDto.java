package net.foodeals.organizationEntity.application.dto.response;

import lombok.Data;
import net.foodeals.contract.domain.entities.enums.ContractStatus;

@Data
public class OrganizationEntityDto {
    private String createdAt;

    private String logoPath;

    private String companyName;

    private String manager;

    private String contractStatus;

    private String email;

    private String phone;
}
