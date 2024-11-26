package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.exceptions.CityNotFoundException;
import net.foodeals.location.domain.repositories.CityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
class CityServiceImpl implements CityService {

    private final ModelMapper modelMapper;
    private final CityRepository repository;
    private final CountryService countryService;


    @Override
    public List<City> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<City> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<City> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public City findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
    }

    @Override
    public City create(CityRequest request) {
        Country country = countryService.findByName(request.country().toLowerCase());
        City city = City.builder().name(request.name().toLowerCase()).build();
        city = this.repository.save(city);
        city.setCountry(country);
        country.getCities().add(city);
        this.countryService.save(country);
        return repository.save(city);
    }

    @Override
    public City update(UUID id, CityRequest request) {
        Country country = countryService.findByName(request.country().toLowerCase());
        City existingCity = repository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));

        if (!country.getName().equals(existingCity.getCountry().getName())) {
            Country country1 = existingCity.getCountry();
            country1.getCities().remove(existingCity);
            existingCity.setCountry(country);
            country.getCities().add(existingCity);
        }
        existingCity.setName(request.name().toLowerCase());
        return repository.save(existingCity);
    }

    @Override
    public void delete(UUID id) {
        if (repository.existsById(id))
            throw new CityNotFoundException(id);

        repository.softDelete(id);
    }

    @Override
    public City findByName(String name) {
        return this.repository.findByName(name.toLowerCase());
    }

    @Override
    public City save(City city) {
        return this.repository.saveAndFlush(city);
    }

    @Override
    public List<Region> getRegions(UUID id) {
        City city = repository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
        return city.getRegions();
    }

    @Override
    public Long count() {
        return this.repository.count();
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
