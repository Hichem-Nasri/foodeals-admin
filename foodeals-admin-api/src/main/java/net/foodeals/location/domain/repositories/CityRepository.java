package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
}
