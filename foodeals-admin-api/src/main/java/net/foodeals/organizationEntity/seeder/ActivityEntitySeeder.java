package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.repositories.CityRepository;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.enums.ActivityType;
import net.foodeals.organizationEntity.domain.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(5)
public class ActivityEntitySeeder {

    @Autowired
    private final ActivityRepository activityRepository;

    public ActivityEntitySeeder(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void ActivitySeeder() {
        if (!this.activityRepository.existsByName("Activity 1")) {
            Activity activity1 = Activity.builder()
                    .name("Activity 1")
                    .type(ActivityType.PARTNER) // Set the type here
                    .build();
            this.activityRepository.save(activity1);
        }

        if (!this.activityRepository.existsByName("Activity 2")) {
            Activity activity2 = Activity.builder()
                    .name("Activity 2")
                    .type(ActivityType.ASSOCIATION) // Set the type here
                    .build();
            this.activityRepository.save(activity2);
        }

        if (!this.activityRepository.existsByName("Activity 3")) {
            Activity activity3 = Activity.builder()
                    .name("Activity 3")
                    .type(ActivityType.PARTNER) // Set the type here
                    .build();
            this.activityRepository.save(activity3);
        }
    }
}
