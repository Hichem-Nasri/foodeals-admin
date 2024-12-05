package net.foodeals.location.application.services.impl;

import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.exceptions.CityNotFoundException;
import net.foodeals.location.domain.repositories.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RegionServiceImpl implements RegionService {
    private final CountryService countryService;
    private final RegionRepository regionRepository;
    private final CityService cityService;


    public RegionServiceImpl(CountryService countryService, RegionRepository regionRepository, CityService cityService) {
        this.countryService = countryService;
        this.regionRepository = regionRepository;
        this.cityService = cityService;
    }

    @Transactional
    public Region findByName(String name) {
        return this.regionRepository.findByName(name.toLowerCase());
    }

    @Transactional
    @Override
    public List<Region> findAll() {
        return List.of();
    }

    @Transactional
    @Override
    public Page<Region> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Transactional
    @Override
    public Page<Region> findAll(Pageable pageable) {
        return this.regionRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Region findById(UUID uuid) {
        return this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));
    }

    @Transactional
    @Override
    public Region create(RegionRequest dto) {
        Country country = this.countryService.findByName(dto.country().toLowerCase());
        State state = country.getStates().stream().filter(s -> s.getName().equals(dto.state().toLowerCase())).findFirst().get();
        City city = state.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
        Region region = Region.builder().name(dto.name().toLowerCase())
                .city(city)
                .build();
        region = this.regionRepository.save(region);
        city.getRegions().add(region);
        this.cityService.save(city);
        return region;
    }

    @Transactional
    @Override
    public Region update(UUID uuid, RegionRequest dto) {
        Region region = this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));
        if (!dto.country().toLowerCase().equals(region.getCity().getState().getCountry().getName())) {
            Country country = this.countryService.findByName(dto.country().toLowerCase());
            State state = country.getStates().stream().filter(s -> s.getName().equals(dto.state().toLowerCase())).findFirst().get();
            City city = state.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
            region.setCity(city);
        } else if (!dto.state().toLowerCase().equals(region.getCity().getState().getName()) || !dto.city().toLowerCase().equals(region.getCity().getName())) {
            State state = region.getCity().getState().getCountry().getStates().stream().filter(s -> s.getName().equals(dto.state().toLowerCase())).findFirst().get();
            City city = state.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
            region.setCity(city);
        }
        region.setName(dto.name().toLowerCase());
        return this.regionRepository.save(region);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        Region region = this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));

        this.regionRepository.softDelete(region.getId());
    }

    @Transactional
    @Override
    public Long count() {
        return this.regionRepository.count();
    }

    @Transactional
    @Override
    public boolean existsByName(String region) {
        return regionRepository.existsByName(region);
    }
}