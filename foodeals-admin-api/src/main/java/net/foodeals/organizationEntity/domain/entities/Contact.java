package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.user.domain.valueObjects.Name;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @Embedded
    private Name name;

    private String email;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = true)
    private OrganizationEntity organizationEntity;

    private boolean isResponsible;

}
