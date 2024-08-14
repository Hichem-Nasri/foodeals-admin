package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;

import java.util.List;

@Entity
@Table(name = "subscription")

@Getter
@Setter
public class Subscription extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "cash_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "cash_currency"))
    })
    private Price cash;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "card_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "card_currency"))
    })
    private Price card;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolutionContract> solutionContracts;
}