package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityAddressDto {

    @NotBlank
    private String address;

    @NotNull
    private String city;
    @NotNull
    private String region;
    @NotBlank
    private String iframe;
}
