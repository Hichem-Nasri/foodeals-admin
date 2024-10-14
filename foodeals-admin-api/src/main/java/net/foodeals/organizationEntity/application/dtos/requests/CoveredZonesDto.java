package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;

import java.util.List;

@Data
public class CoveredZonesDto {

    private String country;

    private String city;

    private List<String> regions;
}
