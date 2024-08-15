package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
