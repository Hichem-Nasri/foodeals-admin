package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscription")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "amount_currency"))
    })
    private Price amount;

    private Integer numberOfDueDates;

    private Float duration;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SolutionContract> solutionContracts = new ArrayList<>();
}