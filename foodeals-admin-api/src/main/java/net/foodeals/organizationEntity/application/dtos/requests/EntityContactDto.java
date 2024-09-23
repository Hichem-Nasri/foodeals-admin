package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.foodeals.user.domain.valueObjects.Name;

@Data
public class EntityContactDto {
    @NotNull
    private Name name;

    @Email
    private String email;

    @NotBlank
    private String phone;
}
