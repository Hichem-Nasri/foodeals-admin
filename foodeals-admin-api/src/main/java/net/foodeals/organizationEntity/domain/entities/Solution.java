package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Table(name = "solutions")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solution extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "solutions")
    @Builder.Default
    private Set<OrganizationEntity> organizationEntities = new HashSet<>();

    @ManyToMany(mappedBy = "solutions")
    @Builder.Default
    private Set<SubEntity> subEntities = new HashSet<>();

}
