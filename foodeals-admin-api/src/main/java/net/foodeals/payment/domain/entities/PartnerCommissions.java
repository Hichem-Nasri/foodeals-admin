package net.foodeals.payment.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentPartner", "subEntityCommissions"})
public class PartnerCommissions extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @Embedded
    private PartnerInfo partnerInfo;

    @Transient
    private PartnerI partner;

    private Date date;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentDirection paymentDirection;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_method_id", referencedColumnName = "id")
    private PaymentMethod paymentMethod;

    private String proofDocument;

    private Date confirmedAt;

    private String emitterName;

    @ManyToOne
    @JoinColumn(name = "parent_partner_id")
    @ToString.Exclude
    private PartnerCommissions parentPartner;

    @OneToMany(mappedBy = "parentPartner")
    @Builder.Default
    @ToString.Exclude
    private Set<PartnerCommissions> subEntityCommissions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private PaymentResponsibility paymentResponsibility;

    @Override
    public UUID getId() {
        return id;
    }
}
