package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class RegionEntitySeeder {

    private final RegionRepository regionRepository;
    private final CityService cityService;


    public RegionEntitySeeder(RegionRepository regionRepository, CityService cityService) {
        this.regionRepository = regionRepository;
        this.cityService = cityService;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void RegionSeeder() {
        City city = this.cityService.findByName("Casablanca");
        Region region = Region.builder().name("maarif")
                .city(city)
                .build();
        this.regionRepository.save(region);
    }

}
