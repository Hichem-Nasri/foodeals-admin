package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class EntityAddressDto {

    @NotBlank
    private String address;

    @NotBlank
    private String country;

    @NotNull
    private String city;
    @NotNull
    private String region;
    @NotBlank
    private String iframe;
}
