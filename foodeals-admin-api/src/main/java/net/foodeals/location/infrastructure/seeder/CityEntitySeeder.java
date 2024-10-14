package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
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
    private CountryService countryService;


    public CityEntitySeeder(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void CitySeeder() {
//        if (this.countryService.count() == 0 ) {
//            CountryRequest countryRequest = new CountryRequest("morocco");
//            Country country = this.countryService.create(countryRequest);
//            this.countryService.save(country);
//            City city1 = City.builder().name("casablanca")
//                    .country(country)
//                    .code("20235")
//                    .build();
//            City city2 = City.builder().name("settat")
//                    .country(country)
//                    .code("20243")
//                    .build();
//            this.cityRepository.saveAllAndFlush(List.of(city2, city1));
//            country.getCities().add(city1);
//            country.getCities().add(city2);
//            this.countryService.save(country);
//        }
//    }
}
