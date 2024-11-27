package net.foodeals.location.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.location.domain.entities.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CountryRepository extends BaseRepository<Country, UUID> {
    @Query("SELECT COUNT(c) FROM City c WHERE c.state.country.name = :countryName")
    int countTotalCitiesByCountryName(@Param("countryName") String countryName);

    Country findByName(String name);
    boolean existsByName(String name);
}
