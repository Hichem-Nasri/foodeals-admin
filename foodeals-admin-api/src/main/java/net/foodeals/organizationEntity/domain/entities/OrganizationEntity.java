package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.notification.domain.entity.Notification;
import net.foodeals.offer.domain.entities.DonorInfo;
import net.foodeals.offer.domain.entities.PublisherI;
import net.foodeals.offer.domain.entities.ReceiverInfo;
import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.offer.domain.enums.DonorType;
import net.foodeals.offer.domain.enums.PublisherType;
import net.foodeals.organizationEntity.application.dtos.requests.CoveredZonesDto;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerI;
import net.foodeals.payment.domain.entities.Payment;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "organization_entities")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEntity extends AbstractEntity<UUID> implements DonorInfo, ReceiverInfo, PublisherI, PartnerI {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "cover_path")
    private String coverPath;

    @Enumerated(EnumType.STRING)
    private EntityType type;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEntity> subEntities = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Activity> activities = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Solution> solutions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;

    private String commercialNumber;

    @Builder.Default
    @OneToMany(mappedBy = "organizationEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Contact> contacts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "bank_information")
    private BankInformation bankInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Contract contract;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Features> features = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerCommissions> commissions = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "organizationEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoveredZones> coveredZones = new ArrayList<>();

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public DonationReceiverType getReceiverType() {
        return switch (this.type) {
            case EntityType.ASSOCIATION -> DonationReceiverType.ASSOCIATION;
            case EntityType.FOOD_BANK -> DonationReceiverType.FOOD_BANK;
            default -> null;
        };
    }

    @Override
    public DonorType getDonorType() {
        return switch (this.type) {
            case EntityType.PARTNER_WITH_SB -> DonorType.PARTNER_WITH_SB;
            case EntityType.NORMAL_PARTNER -> DonorType.NORMAL_PARTNER;
            case EntityType.FOOD_BANK -> DonorType.FOOD_BANK;
            default -> null;
        };
    }

    public PartnerType getPartnerType() {
        return switch (type) {
            case EntityType.PARTNER_WITH_SB -> PartnerType.PARTNER_SB;
            case EntityType.NORMAL_PARTNER -> PartnerType.NORMAL_PARTNER;
            case EntityType.DELIVERY_PARTNER -> PartnerType.DELIVERY_PARTNER;
            default -> null;
        };
    }

    @Override
    public PublisherType getPublisherType() {
        return switch (this.type) {
            case EntityType.PARTNER_WITH_SB -> PublisherType.PARTNER_WITH_SB;
            case EntityType.NORMAL_PARTNER -> PublisherType.NORMAL_PARTNER;
            case EntityType.FOOD_BANK -> PublisherType.FOOD_BANK;
            default -> null;
        };
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAvatarPath() {
        return this.avatarPath;
    }

    @Override
    @Transactional
    public boolean commissionPayedBySubEntities() {
        return this.contract.isCommissionPayedBySubEntities();
    }

    @Override
    @Transactional
    public boolean subscriptionPayedBySubEntities() {
        return this.contract.isSubscriptionPayedBySubEntities();
    }

    @Override
    public boolean singleSubscription() {
        return this.contract.isSingleSubscription();
    }

    public List<CoveredZonesDto> getCoveredZonesDto() {
        return coveredZones.stream()
                .map(coveredZone -> CoveredZonesDto.builder()
                        .country(coveredZone.getRegion().getCity().getCountry().getName())
                        .city(coveredZone.getRegion().getCity().getName())
                        .regions(List.of(coveredZone.getRegion().getName()))
                        .build())
                .collect(Collectors.groupingBy(CoveredZonesDto::getCity))
                .entrySet()
                .stream()
                .map(entry -> CoveredZonesDto.builder()
                        .city(entry.getKey())
                        .country(entry.getValue().get(0).getCountry()) // Extract country from any dto in the group
                        .regions(entry.getValue().stream()
                                .flatMap(dto -> dto.getRegions().stream())
                                .distinct()
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}