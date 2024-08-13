package net.foodeals.user.domain.entities;

import java.util.List;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

/**
 * Permission
 */
@Entity
@Table(name = "permissions")

@Getter
@Setter
public class Permission extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

    @ManyToMany
    private List<Role> roles;
}
