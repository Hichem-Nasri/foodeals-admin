package net.foodeals.user.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
    private String query;
    private List<EntityType> types;

    // getters and setters
}
