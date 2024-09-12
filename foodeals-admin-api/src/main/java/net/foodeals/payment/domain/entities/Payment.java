package net.foodeals.payment.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organizationEntity_id")
    private OrganizationEntity organizationEntity;

    @ManyToOne
    @JoinColumn(name = "subEntity_id")
    private SubEntity subEntity;

    private String date;

    private Long numberOfOrders;

    private Double paymentsWithCard;

    private Double paymentsWithCash;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PartnerType partnerType;
}
