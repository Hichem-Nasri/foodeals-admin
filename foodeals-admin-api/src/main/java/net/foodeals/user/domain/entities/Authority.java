package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.List;

/**
 * Permission
 */
@Entity
@Table(name = "permissions")

@Getter
@Setter
public class Authority extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

    @ManyToMany
    private List<Role> roles;
}
