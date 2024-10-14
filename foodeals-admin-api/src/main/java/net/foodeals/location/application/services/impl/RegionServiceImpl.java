package net.foodeals.location.application.services.impl;

import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.exceptions.CityNotFoundException;
import net.foodeals.location.domain.repositories.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public Region findByName(String name) {
        return this.regionRepository.findByName(name.toLowerCase());
    }

    @Override
    public List<Region> findAll() {
        return List.of();
    }

    @Override
    public Page<Region> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<Region> findAll(Pageable pageable) {
        return this.regionRepository.findAll(pageable);
    }

    @Override
    public Region findById(UUID uuid) {
        return this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));
    }

    @Override
    public Region create(RegionRequest dto) {
        Country country = this.countryService.findByName(dto.country().toLowerCase());
        City city = country.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
        Region region = Region.builder().name(dto.name().toLowerCase())
                .city(city)
                .build();
        region = this.regionRepository.save(region);
        city.getRegions().add(region);
        this.cityService.save(city);
        return region;
    }

    @Override
    public Region update(UUID uuid, RegionRequest dto) {
        Region region = this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));

        if (!dto.country().toLowerCase().equals(region.getCity().getCountry().getName())) {
            Country country = this.countryService.findByName(dto.country().toLowerCase());
            City city = country.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
            region.setCity(city);
        } else if (!dto.city().toLowerCase().equals(region.getCity().getName())) {
            Country country = region.getCity().getCountry();
            City city = country.getCities().stream().filter(c -> c.getName().equals(dto.city().toLowerCase())).findFirst().get();
            region.setCity(city);
        }
        region.setName(dto.name().toLowerCase());
        return this.regionRepository.save(region);
    }

    @Override
    public void delete(UUID uuid) {
        Region region = this.regionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with Id : " + uuid + " not found"));

        this.regionRepository.softDelete(region.getId());
    }
}
