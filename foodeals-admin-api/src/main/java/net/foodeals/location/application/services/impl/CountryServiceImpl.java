package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.exceptions.CountryNotFoundException;
import net.foodeals.location.domain.repositories.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class CountryServiceImpl {
    private final CountryRepository repository;
    private final ModelMapper modelMapper;

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Country findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));
    }

    public Country save(CountryRequest request) {
        Country country = modelMapper.map(request, Country.class);
        return repository.save(country);
    }

    public Country update(CountryRequest request, UUID id) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));

        modelMapper.map(request, existingCountry);
        return repository.save(existingCountry);
    }

    public void deleteById(UUID id) {
        if (!repository.existsById(id))
            throw new CountryNotFoundException(id);

        repository.softDelete(id);
    }
}   
