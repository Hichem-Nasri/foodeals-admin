package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Box;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoxRepository extends JpaRepository<Box, UUID> {
}
