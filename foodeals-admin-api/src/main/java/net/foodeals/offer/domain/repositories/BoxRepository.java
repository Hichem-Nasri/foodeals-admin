package net.foodeals.offer.domain.repositories;

import net.foodeals.offer.domain.entities.Box;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepository extends JpaRepository<Box, Long> {
}
