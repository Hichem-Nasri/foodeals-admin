package net.foodeals.location.infrastructure.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.StateService;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class StateSeeder {
    @Autowired
    private StateService stateService;

    @Autowired
    private CountryService countryService;
//
//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void createState() {
//        Country country = this.countryService.findByName("Morocco");
//        StateRequest stateRequest = new StateRequest("Casablanca-Settat", "102436", country.getId());
//        State state = this.stateService.create(stateRequest);
//        country.getStates().add(state);
//        this.countryService.save(country);
//    }
}
