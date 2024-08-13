package net.foodeals.entity.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.entity.domain.enums.EntityType;
import net.foodeals.entity.domain.valueObject.Coordinates;
import net.foodeals.user.domain.entities.User;

import java.util.List;

@jakarta.persistence.Entity
@Table(name = "entities")

@Getter @Setter
public class Entity extends AbstractEntity<Long> {

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

    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEntity> subEntities;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    private User user;
}
