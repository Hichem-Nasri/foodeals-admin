package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.StateService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class CityEntitySeeder {

    @Autowired
    private final CityRepository cityRepository;

    @Autowired
    private StateService stateService;
    @Autowired
    private CountryService countryService;


    public CityEntitySeeder(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void CitySeeder() {


        CountryRequest countryRequest = new CountryRequest("Morocco", "202410");
        Country country = this.countryService.create(countryRequest);

        StateRequest stateRequest = new StateRequest("Casablanca-Settat", "102436", country.getId());
        State state = this.stateService.create(stateRequest);
        country.getStates().add(state);
        this.countryService.save(country);

        if (this.cityRepository.count() == 0) {
            City city1 = City.builder().name("Casablanca")
                    .state(state)
                    .code("20235")
                    .build();
            City city2 = City.builder().name("Settat")
                    .state(state)
                    .code("20243")
                    .build();
            this.cityRepository.saveAllAndFlush(List.of(city2, city1));
            state.getCities().add(city1);
            state.getCities().add(city2);
            this.stateService.save(state);
        }
    }
}
