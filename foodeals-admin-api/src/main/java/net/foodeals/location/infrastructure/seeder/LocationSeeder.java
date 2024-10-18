package net.foodeals.location.infrastructure.seeder;

import jakarta.persistence.EntityManager;
import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class LocationSeeder {

    private final CountryService countryService;
    private final CityService cityService;
    private final RegionService regionService;
    private final EntityManager entityManager;

    @Autowired
    public LocationSeeder(CountryService countryService, CityService cityService, RegionService regionService, EntityManager entityManager) {
        this.countryService = countryService;
        this.cityService = cityService;
        this.regionService = regionService;
        this.entityManager = entityManager;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        if (!this.countryService.existsByName("morocco")) {
            CountryRequest countryRequest = new CountryRequest("Morocco");
            this.countryService.create(countryRequest);
            System.out.println("Country created");
        }

// Check if city exists and create if not
        if (!this.cityService.existsByName("casablanca")) {
            CityRequest cityRequest = new CityRequest("Morocco", "Casablanca");
            this.cityService.create(cityRequest);
            System.out.println("City created");
        }

// Check if region exists and create if not
        if (!this.regionService.existsByName("maarif")) {
            RegionRequest regionRequest = new RegionRequest("Morocco", "Casablanca", "Maarif");
            this.regionService.create(regionRequest);
            entityManager.flush();
            System.out.println("Region created");
        }
    }
}
