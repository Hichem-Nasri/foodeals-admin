package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Builder;
import lombok.Data;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrganizationEntityFilter {
    private Instant startDate;
    private Instant endDate;
    private List<EntityType> types;
    private List<String> names;
    private List<String> solutions;
    private String email;
    private String phone;
    private UUID cityId;
    private Long collabId;
}