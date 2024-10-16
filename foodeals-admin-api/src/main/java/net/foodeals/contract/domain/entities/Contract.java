package net.foodeals.contract.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contracts")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    @Lob
    @JsonIgnore
    private byte[] document;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL)
    @JsonIgnore
    private UserContract userContracts;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SolutionContract> solutionContracts = new ArrayList<>();

    @JsonIgnore
    private Integer maxNumberOfSubEntities;

    @JsonIgnore
    private Float minimumReduction;

    @OneToOne(mappedBy = "contract", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private OrganizationEntity organizationEntity;

    private boolean singleSubscription;

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    private boolean subscriptionPayedBySubEntities;
}
