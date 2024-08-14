package net.foodeals.contract.domain.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.user.domain.entities.User;

import java.util.List;

@Entity
@Table(name = "contracts")

@Getter
@Setter
public class Contract extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String content;

    @ManyToMany
    private List<User> users;
}
