package net.foodeals.crm.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.CountryResponse;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.dtos.responses.StateResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProspectAddress {
    private String address;

    private CountryResponse country;

    private CityResponse city;

    private StateResponse state;

    private RegionResponse region;

    private String iframe;
}
