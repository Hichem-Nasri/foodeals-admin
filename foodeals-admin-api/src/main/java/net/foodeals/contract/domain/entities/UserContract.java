package net.foodeals.contract.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserContract extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false )
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

}
