package net.foodeals.location.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.List;

@Entity
@Table(name = "cities")

@Getter
@Setter
public class City extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private State state;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;
}