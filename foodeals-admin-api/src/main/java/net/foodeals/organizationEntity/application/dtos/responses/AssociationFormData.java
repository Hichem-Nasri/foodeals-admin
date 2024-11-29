package net.foodeals.organizationEntity.application.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.user.application.dtos.responses.UserInfoDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociationFormData {
    private UUID id;
    private String name;
    private String avatarPath;
    private String coverPath;
    private EntityType type;
    private String Pv;
    private EntityAddressDto address;
    private ContactDto contactDto;
    private UserInfoDto manager;
    private List<String> solutions;
    private Integer numberOfPoints;
    private ContractStatus status;
    private List<String> activities;
}
