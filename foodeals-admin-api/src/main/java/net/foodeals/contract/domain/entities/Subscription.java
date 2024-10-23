package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
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

    private LocalDate startDate;

    private Integer duration;

    private LocalDate endDate;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SolutionContract> solutionContracts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Deadlines> deadlines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    @ManyToOne
    @JoinColumn(name = "organizationEntity_id")
    private OrganizationEntity organizationEntity;

    @ManyToOne
    @JoinColumn(name = "subEntity_id")
    private SubEntity subEntity;


    @Enumerated(EnumType.STRING)
    private PartnerType partnerType;

}