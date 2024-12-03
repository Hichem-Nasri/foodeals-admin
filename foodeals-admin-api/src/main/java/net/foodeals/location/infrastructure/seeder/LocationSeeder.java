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

// Create states and associated cities and regions
        String[] states = {"casablanca-settat", "rabat-salé-kénitra", "marrakesh-safi"};
        String[][] cities = {{"casablanca", "settat", "mohammedia"}, {"rabat", "salé", "kénitra"}, {"marrakesh", "safi", "essaouira"}};
        String[][][] regions = {
                {{"maarif", "hay hassani", "anfa"}, {"sidi bouzid", "sidi yahya", "bouznika"}, {"ain sebaa", "bouskoura", "had soualem"}},
                {{"agdal", "hay riad", "souissi"}, {"tabriquet", "bettana", "hassan"}, {"mehdia", "gharb", "larache"}},
                {{"guéliz", "medina", "menara"}, {"sidi bouzid", "jamaâ el fna", "kasbah"}, {"zerktouni", "sidi youssef ben ali", "chichaoua"}}
        };

        for (int i = 0; i < states.length; i++) {
            State existingState = this.stateService.findByName(states[i]);
            if (existingState == null) {
                StateRequest stateRequest = new StateRequest(states[i], "morocco");
                this.stateService.create(stateRequest);
                System.out.println("State '" + states[i] + "' created");
            }

            State state = this.stateService.findByName(states[i]);

            for (int j = 0; j < cities[i].length; j++) {
                if (!this.cityService.existsByName(cities[i][j])) {
                    CityRequest cityRequest = new CityRequest("morocco", states[i], cities[i][j]);
                    this.cityService.create(cityRequest);
                    System.out.println("City '" + cities[i][j] + "' created");
                }

                City city = this.cityService.findByName(cities[i][j]);

                for (int k = 0; k < regions[i][j].length; k++) {
                    if (!this.regionService.existsByName(regions[i][j][k])) {
                        RegionRequest regionRequest = new RegionRequest("morocco", states[i], cities[i][j], regions[i][j][k]);
                        this.regionService.create(regionRequest);
                        entityManager.flush();
                        System.out.println("Region '" + regions[i][j][k] + "' created");
                    }
                }
            }
        }
    }
}