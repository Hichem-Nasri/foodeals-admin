package net.foodeals.location.application.services;

import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.repositories.RegionRepository;
import org.springframework.stereotype.Service;

@Service
public class RegionService {
    private final RegionRepository regionRepository;
    private final CityService cityService;


    public RegionService(RegionRepository regionRepository, CityService cityService) {
        this.regionRepository = regionRepository;
        this.cityService = cityService;
    }

    public Region findByName(String name) {
        return this.regionRepository.findByName(name);
    }

    public Region create(String cityName, String regionName) {
        City city = this.cityService.findByName(cityName);
        Region region = Region.builder().name(regionName)
                .city(city)
                .build();
        this.regionRepository.save(region);
        city.getRegions().add(region);
        this.cityService.save(city);
        return region;
    }
}
