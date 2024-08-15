package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Role
 */
@Entity
@Table(name = "roles")

@Getter
@Setter
public class Role extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Authority> authorities = new ArrayList<>();
}
