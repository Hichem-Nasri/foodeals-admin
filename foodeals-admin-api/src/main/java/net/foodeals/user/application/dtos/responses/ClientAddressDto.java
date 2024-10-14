package net.foodeals.user.application.dtos.responses;

import lombok.Data;

@Data
public class ClientAddressDto {

    private String country;

    private String city;

    private String region;

    private String address;
}
