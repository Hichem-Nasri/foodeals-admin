package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Coordinates;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.organizationEntity.domain.enums.EntityType;
import net.foodeals.user.domain.entities.User;

import java.util.ArrayList;
import java.util.List;

@jakarta.persistence.Entity
@Table(name = "entities")

@Getter @Setter
public class OrganizationEntity extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "cover_path")
    private String coverPath;

    @Embedded
    private Coordinates coordinates;

    @Enumerated
    private EntityType type;

    @OneToMany(mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEntity> subEntities = new ArrayList<>();

    @ManyToMany
    private List<Activity> activities = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address address;

    @ManyToMany
    private List<Solution> solutions = new ArrayList<>();
}