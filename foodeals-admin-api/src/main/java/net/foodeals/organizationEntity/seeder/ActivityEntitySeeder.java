package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.repositories.CityRepository;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class ActivityEntitySeeder {

    @Autowired
    private final ActivityRepository activityRepository;

    public ActivityEntitySeeder(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void ActivitySeeder() {
        if (this.activityRepository.count() == 0) {
            Activity activity = Activity.builder().name("Activity 1")
                    .build();
            Activity activity1 = Activity.builder().name("Activity 2")
                    .build();
            Activity activity2 = Activity.builder().name("Activity 3")
                    .build();
            this.activityRepository.saveAll(List.of(activity1, activity2, activity));
        }
    }
}
