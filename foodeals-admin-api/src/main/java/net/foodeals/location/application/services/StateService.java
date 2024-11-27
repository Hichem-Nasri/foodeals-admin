package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.entities.Region;

import java.util.List;
import java.util.UUID;

public interface StateService extends CrudService<State, UUID, StateRequest> {
    State findByName(String name);

    State save(State state);

    List<City> getCities(UUID id);

    Long count();

    boolean existsByName(String name);
}
