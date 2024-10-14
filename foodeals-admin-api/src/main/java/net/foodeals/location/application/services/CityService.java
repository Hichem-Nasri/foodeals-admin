package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface CityService extends CrudService<City, UUID, CityRequest> {
    City findByName(String name);

    City save(City city);

    List<Region> getRegions(UUID id);
}
