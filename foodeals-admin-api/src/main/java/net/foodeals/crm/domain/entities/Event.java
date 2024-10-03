package net.foodeals.crm.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    private User lead;

    private String dateAndHour;

    private String object;

    @Column(length = 200000)
    private String message;
}
