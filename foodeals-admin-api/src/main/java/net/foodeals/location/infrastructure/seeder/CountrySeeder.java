package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class CountrySeeder {

    @Autowired
    private CountryService countryService;

//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void createCountry() {
//        CountryRequest countryRequest = new CountryRequest("Morocco", "202410");
//        Country country = this.countryService.create(countryRequest);
//    }
}
