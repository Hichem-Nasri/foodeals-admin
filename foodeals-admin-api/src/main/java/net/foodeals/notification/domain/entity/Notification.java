package net.foodeals.notification.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
public class Notification extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID    Id;

    private String  title;

    private String  content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    private OrganizationEntity organizationEntity;

    @ManyToOne
    @JoinColumn(name = "subEntity_id", nullable = false)
    private SubEntity subEntity;

}
