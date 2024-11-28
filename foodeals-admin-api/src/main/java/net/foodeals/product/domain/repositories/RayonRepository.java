package net.foodeals.product.domain.repositories;

import net.foodeals.product.domain.entities.Rayon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RayonRepository extends JpaRepository<Rayon, UUID> {

    Rayon findByName(String name);
}
