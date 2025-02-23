package net.foodeals.location.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.State;

import java.util.UUID;

public interface StateRepository extends BaseRepository<State, UUID> {
    State findByName(String name);
    boolean existsByName(String name);
}
