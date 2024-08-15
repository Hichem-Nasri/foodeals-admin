package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
