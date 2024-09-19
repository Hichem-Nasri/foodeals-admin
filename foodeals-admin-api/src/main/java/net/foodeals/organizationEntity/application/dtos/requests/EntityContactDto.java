package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;
import net.foodeals.user.domain.valueObjects.Name;

@Data
public class EntityContactDto {

    private Name name;

    private String email;

    private String phone;
}
