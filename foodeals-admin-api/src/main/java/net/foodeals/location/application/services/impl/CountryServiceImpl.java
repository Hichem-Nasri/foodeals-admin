package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.exceptions.CountryNotFoundException;
import net.foodeals.location.domain.repositories.CountryRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class CountryServiceImpl implements CountryService {
    private final CountryRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public List<Country> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Country> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Country> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Country findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));
    }

    @Override
    public Country create(CountryRequest request) {
        Country country = Country.builder().name(request.name().toLowerCase())
                .build();
        return repository.saveAndFlush(country);
    }

    @Override
    public Country update(UUID id, CountryRequest request) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));

        existingCountry.setName(request.name().toLowerCase());
        return repository.save(existingCountry);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new CountryNotFoundException(id);

        repository.softDelete(id);
    }

    @Override
    public int countTotalCitiesByCountryName(String name) {
        return this.repository.countTotalCitiesByCountryName(name.toLowerCase());
    }

    @Override
    public Country findByName(String name) {
        return this.repository.findByName(name.toLowerCase());
    }

    @Override
    public Country save(Country country) {
        return this.repository.saveAndFlush(country);
    }

    @Override
    public Long count() {
        return this.repository.count();
    }

    @Override
    public List<State> getStates(UUID id) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));
        return existingCountry.getStates();
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}

