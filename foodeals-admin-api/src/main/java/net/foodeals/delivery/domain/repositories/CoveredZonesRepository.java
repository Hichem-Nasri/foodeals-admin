package net.foodeals.delivery.domain.repositories;


import net.foodeals.delivery.domain.entities.CoveredZones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoveredZonesRepository extends JpaRepository<CoveredZones, UUID> {
}
