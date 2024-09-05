package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.notification.domain.entity.Notification;
import net.foodeals.organizationEntity.enums.EntityType;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Table(name = "organization_entities")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "cover_path")
    private String coverPath;

    @Enumerated
    private EntityType type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEntity> subEntities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Activity mainActivity;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Activity> subActivities = new HashSet<>();

    @OneToMany(mappedBy = "organizationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Solution> solutions = new HashSet<>();

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

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Features> features;
}