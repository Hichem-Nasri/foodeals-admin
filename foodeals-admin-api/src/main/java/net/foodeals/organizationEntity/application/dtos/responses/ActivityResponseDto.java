package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class ActivityResponseDto {
    private UUID id;

    private String name;
}
