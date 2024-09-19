package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CityEntitySeeder {

    @Autowired
    private final CityRepository cityRepository;


    public CityEntitySeeder(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void CitySeeder() {
        if (this.cityRepository.count() == 0) {
            City city = City.builder().name("casablanca")
                    .code("20235")
                    .build();
            this.cityRepository.save(city);
        }
    }
}
