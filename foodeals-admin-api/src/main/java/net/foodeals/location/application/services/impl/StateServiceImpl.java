package net.foodeals.location.application.services.impl;

import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.StateService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.exceptions.CityNotFoundException;
import net.foodeals.location.domain.exceptions.StateNotFoundException;
import net.foodeals.location.domain.repositories.StateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
class StateServiceImpl implements StateService {

    private final ModelMapper modelMapper;
    private final StateRepository repository;
    private final CountryService countryService;

    @Override
    public List<State> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<State> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<State> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public State findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new StateNotFoundException(id));
    }
    @Override
    public State create(StateRequest request) {
        Country country = countryService.findByName(request.country().toLowerCase());
        State state = State.builder().name(request.name().toLowerCase()).build();
        state = this.repository.save(state);
        state.setCountry(country);
        country.getStates().add(state);
        this.countryService.save(country);
        return repository.save(state);
    }

    @Override
    public State update(UUID id, StateRequest request) {
        Country country = countryService.findByName(request.country().toLowerCase());
        State existingState = repository.findById(id)
                .orElseThrow(() -> new StateNotFoundException(id));

        if (!country.getName().equals(existingState.getCountry().getName())) {
            Country country1 = existingState.getCountry();
            country1.getStates().remove(existingState);
            existingState.setCountry(country);
            country.getStates().add(existingState);
        }
        existingState.setName(request.name().toLowerCase());
        return repository.save(existingState);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new StateNotFoundException(id);
        repository.deleteById(id);
    }

    @Override
    public State findByName(String name) {
        return repository.findByName(name.toLowerCase());
    }

    @Override
    public State save(State state) {
        return this.repository.saveAndFlush(state);
    }

    @Override
    public List<City> getCities(UUID id) {
        State state = repository.findById(id)
                .orElseThrow(() -> new StateNotFoundException(id));
        return state.getCities();
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}