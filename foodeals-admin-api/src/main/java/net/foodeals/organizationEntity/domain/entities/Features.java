package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Features extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "features")
    @Builder.Default
    private Set<OrganizationEntity> organizationEntities = new HashSet<>();
}
