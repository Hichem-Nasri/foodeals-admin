package net.foodeals.user.domain.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

/**
 * Permission
 */
@Entity
@Table(name = "permissions")

@Getter
@Setter
public class Authority extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    private String value;

    @ManyToMany
    private List<Role> roles;
}
