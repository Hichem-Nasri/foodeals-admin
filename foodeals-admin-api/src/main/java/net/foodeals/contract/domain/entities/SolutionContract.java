package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "solution_contracts")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolutionContract extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String duration;

    @ManyToOne
    private Solution solution;

    @ManyToOne(cascade = CascadeType.ALL)
    private Contract contract;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "comission_id", nullable = true)
    private Commission commission;

    @ManyToOne(cascade = CascadeType.ALL)
    private Subscription subscription;
}