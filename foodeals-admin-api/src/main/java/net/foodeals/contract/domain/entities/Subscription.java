package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.config.BeanUtil;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerI;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


// solution contract -> subscrip ->

@Entity
@Table(name = "subscription")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Subscription extends AbstractEntity<UUID> {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToMany
    @Builder.Default
    private Set<Solution> solutions = new HashSet<>();

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

    @ToString.Exclude
    @OneToMany(mappedBy = "subscription", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SolutionContract> solutionContracts = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Deadlines> deadlines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    @Embedded
    private PartnerInfo partner;

    @Transient
    private PartnerI partnerI;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "parent_partner_id")
    private Subscription parentPartner;

    @ToString.Exclude
    @OneToMany(mappedBy = "parentPartner")
    @Builder.Default
    private Set<Subscription> subEntities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}