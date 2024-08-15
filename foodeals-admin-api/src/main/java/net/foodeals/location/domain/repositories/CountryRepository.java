package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID> {
}
