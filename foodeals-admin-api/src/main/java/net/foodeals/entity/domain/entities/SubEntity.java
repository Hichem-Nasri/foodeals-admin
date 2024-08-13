package net.foodeals.entity.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.entity.domain.enums.EntityType;
import net.foodeals.entity.domain.valueObject.Coordinates;
import net.foodeals.user.domain.entities.User;

@Entity
@Table(name = "sub_entities")

@Getter
@Setter
public class SubEntity extends AbstractEntity<Long> {

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    private net.foodeals.entity.domain.entities.Entity entity;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}