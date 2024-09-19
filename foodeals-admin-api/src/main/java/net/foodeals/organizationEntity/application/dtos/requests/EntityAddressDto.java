package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;

@Data
public class EntityAddressDto {

    private String address;

    private String city;

    private String region;
}
