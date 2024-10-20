package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.processors.annotations.Processable;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityAddressDto {

    @NotBlank
    private String address;

    @Processable
    @NotBlank
    private String country;


    @Processable
    @NotNull
    private String city;


    @Processable
    @NotNull
    private String region;


    @Processable
    @NotBlank
    private String iframe;
}
