package net.foodeals.location.infrastructure.seeder;

import jakarta.persistence.EntityManager;
import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.application.services.StateService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
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
    private final StateService stateService;

    @Autowired
    public LocationSeeder(CountryService countryService, CityService cityService, RegionService regionService, EntityManager entityManager, StateService stateService) {
        this.countryService = countryService;
        this.cityService = cityService;
        this.regionService = regionService;
        this.entityManager = entityManager;
        this.stateService = stateService;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        if (!this.countryService.existsByName("morocco")) {
            CountryRequest countryRequest = new CountryRequest("Morocco");
            this.countryService.create(countryRequest);
            System.out.println("Country created");
        }

// Assuming you have a method to find a country by its name
        Country country = this.countryService.findByName("morocco"); // Replace with the actual country name

// Check if the state "Casablanca-Settat" exists
        State existingState = this.stateService.findByName("Casablanca-Settat");
        if (existingState == null) {
            StateRequest stateRequest = new StateRequest( "Casablanca-Settat", "morocco"); // Pass the country ID
            this.stateService.create(stateRequest);
            System.out.println("State 'Casablanca-Settat' created");
        }

// Now check for the city "Casablanca"
        State state = this.stateService.findByName("Casablanca-Settat"); // Get the state again after creation
        if (!this.cityService.existsByName("Casablanca")) {
            CityRequest cityRequest = new CityRequest("morocco","Casablanca-Settat",  "Casablanca"); // Pass the state ID
            this.cityService.create(cityRequest);
            System.out.println("City 'Casablanca' created");
        }

// Check if region exists and create if not
        City city = this.cityService.findByName("Casablanca-Settat"); // Get the state again after creation
        if (!this.regionService.existsByName("maarif")) {
            RegionRequest regionRequest = new RegionRequest("morocco","Casablanca-Settat", "Casablanca", "Maarif");
            this.regionService.create(regionRequest);
            entityManager.flush();
            System.out.println("Region created");
        }
    }
}
