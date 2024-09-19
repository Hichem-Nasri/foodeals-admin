package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deadlines extends AbstractEntity<UUID> {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private LocalDate dueDate;

    @Embedded
    private Price amount;

    @Enumerated(EnumType.STRING)
    private DeadlineStatus status;

    private LocalDate paymentDate;
}
