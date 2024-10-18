package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface CountryService extends CrudService<Country, UUID, CountryRequest> {
    int countTotalCitiesByCountryName(String name);

    Country findByName(String name);

    Country save(Country country);

    public Long count();

    List<City> getCities(UUID id);

    boolean existsByName(String name);
}
