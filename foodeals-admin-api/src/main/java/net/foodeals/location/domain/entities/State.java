package net.foodeals.location.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.List;

@Entity
@Table(name = "states")

@Getter
@Setter
public class State extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String code;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<City> cities;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Country country;
}
