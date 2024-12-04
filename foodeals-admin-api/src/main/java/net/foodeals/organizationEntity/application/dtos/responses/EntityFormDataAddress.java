package net.foodeals.organizationEntity.application.dtos.responses;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.CountryResponse;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.dtos.responses.StateResponse;

@Data
public class EntityFormDataAddress {

    private String address;

    private CountryResponse country;

    private CityResponse city;

    private StateResponse state;

    private RegionResponse region;

    private String iframe;
}
