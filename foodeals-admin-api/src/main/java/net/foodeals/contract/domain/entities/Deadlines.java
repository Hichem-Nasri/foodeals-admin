package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.config.BeanUtil;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PaymentMethod;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Deadlines extends AbstractEntity<UUID> {
    @Id
    @UuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Embedded
    private Price amount;


    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private DeadlineStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_method_id", referencedColumnName = "id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    private User emitter;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "parent_partner_id")
    private Deadlines parentPartner;

    @ToString.Exclude
    @OneToMany(mappedBy = "parentPartner")
    @Builder.Default
    private Set<Deadlines> subEntityDeadlines = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private PaymentResponsibility paymentResponsibility;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deadlines)) return false;
        Deadlines deadlines = (Deadlines) o;
        return Objects.equals(getId(), deadlines.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}