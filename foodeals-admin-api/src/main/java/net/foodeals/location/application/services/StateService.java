package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.domain.entities.State;

import java.util.UUID;

public interface StateService extends CrudService<State, UUID, StateRequest> {
    State save(State state);

    State findByName(String name);

    State findByCode(String number);
}
