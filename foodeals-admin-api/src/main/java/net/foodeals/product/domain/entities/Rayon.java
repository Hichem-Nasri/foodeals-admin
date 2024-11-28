package net.foodeals.product.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.user.domain.entities.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rayon extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "rayon")
    private List<User> users;

}
