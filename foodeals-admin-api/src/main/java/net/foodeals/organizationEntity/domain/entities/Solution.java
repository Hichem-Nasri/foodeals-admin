package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solutions")

@Getter
@Setter
public class Solution extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "solutions")
    private List<OrganizationEntity> organizationEntities = new ArrayList<>();

    @ManyToMany(mappedBy = "solutions")
    private List<SubEntity> subEntities = new ArrayList<>();
}
