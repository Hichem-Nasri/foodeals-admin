package net.foodeals.user.application.dtos.responses;

import lombok.Data;

@Data
public class ClientAddressDto {

    private String country;

    private String city;

    private String state;

    private String address;
}
