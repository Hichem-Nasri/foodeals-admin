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
//
//        if (this.regionRepository.count() == 0) {
//            City city1 = this.cityService.findByName("casablanca");
//            Region region1 = Region.builder().name("maarif")
//                    .city(city1)
//                    .build();
//            this.regionRepository.save(region1);
//            city1.getRegions().add(region1);
//            this.cityService.save(city1);
//
//            City city2 = this.cityService.findByName("settat");
//            Region region2 = Region.builder().name("region")
//                    .city(city2)
//                    .build();
//            this.regionRepository.saveAndFlush(region2);
//            city2.getRegions().add(region2);
//            this.cityService.save(city2);
//        }
    }

}
