package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Builder;
import lombok.Data;
import net.foodeals.processors.annotations.Processable;

import java.util.List;

@Data
@Builder
public class CoveredZonesDto {

    @Processable
    private String country;

    @Processable
    private String state;

    @Processable
    private String city;

    @Processable
    private List<String> regions;
}
