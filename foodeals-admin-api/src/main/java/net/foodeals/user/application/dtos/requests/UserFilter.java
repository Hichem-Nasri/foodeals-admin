package net.foodeals.user.application.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserFilter {
    private UUID subentityId;
    private List<String> names;
    private String phone;
    private String city;
    private String region;
    private String email;
    private String roleName;
    private List<EntityType> entityTypes;
    private List<SubEntityType> subEntityTypes;
    private List<String> solutions;
    private Instant startDate;
    private Instant endDate;
    private Boolean deletedAt;
}
