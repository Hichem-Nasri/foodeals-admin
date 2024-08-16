package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "solution_contracts")

@Getter
@Setter
public class SolutionContract extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Solution solution;

    @ManyToOne(cascade = CascadeType.ALL)
    private Contract contract;

    @ManyToOne(cascade = CascadeType.ALL)
    private Commission commission;

    @ManyToOne(cascade = CascadeType.ALL)
    private Subscription subscription;
}
