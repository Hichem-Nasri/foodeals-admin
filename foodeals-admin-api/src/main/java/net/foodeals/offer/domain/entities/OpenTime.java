package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table

@Getter
@Setter
public class OpenTime extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String day;

    private String from;

    private String to;

    @OneToMany(mappedBy = "openTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Offer> offers = new ArrayList<Offer>();
}
