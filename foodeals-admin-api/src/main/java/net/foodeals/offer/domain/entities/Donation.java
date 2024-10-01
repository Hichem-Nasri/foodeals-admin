package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.offer.domain.enums.DonationStatus;
import net.foodeals.offer.domain.valueObject.Offerable;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
public class Donation extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpenTime> openTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private Activity activity;

    @Embedded
    private Donor donor;

    @Transient
    private DonorInfo donorInfo;

    @Embedded
    private Offerable offerable;

    @Transient
    private IOfferChoice offerChoice;

    private DonationStatus status;

    @Embedded
    private Receiver receiver;

    @Transient
    private ReceiverInfo ReceiverInfo;

    @Column(length = 20000)
    private String description;
}