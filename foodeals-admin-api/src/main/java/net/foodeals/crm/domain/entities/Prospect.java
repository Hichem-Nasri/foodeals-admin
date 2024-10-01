package net.foodeals.crm.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Builder
@Data
@Entity
public class Prospect extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    private String companyName;

    @ManyToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Activity> activities = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;

    @Builder.Default
    @OneToMany(mappedBy = "prospect", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_id", nullable = true)
    private User lead;

    @Enumerated(EnumType.STRING)
    private ProspectStatus status;
}
