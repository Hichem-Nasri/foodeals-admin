package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.user.domain.entities.User;

import java.time.DayOfWeek;
import java.lang.String;
import java.util.UUID;

@Entity
@Table(name = "working_hours")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHours extends AbstractEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "morning_start")
    private String morningStart;

    @Column(name = "morning_end")
    private String morningEnd;

    @Column(name = "afternoon_start")
    private String afternoonStart;

    @Column(name = "afternoon_end")
    private String afternoonEnd;
}