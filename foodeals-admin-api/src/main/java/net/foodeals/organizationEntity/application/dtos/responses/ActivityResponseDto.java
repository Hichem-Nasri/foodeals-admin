package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.foodeals.organizationEntity.domain.entities.enums.ActivityType;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ActivityResponseDto {
    private UUID id;

    private String name;

    private ActivityType type;
}
