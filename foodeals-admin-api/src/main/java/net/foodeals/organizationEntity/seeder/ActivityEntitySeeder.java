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
import java.util.stream.Collectors;

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
        if (activityRepository.count() == 0) {
            List<String> names = List.of("supermarché", "pâtisserie", "boulangerie", "épicerie", "confiserie");
            List<Activity> activities = names.stream()
                    .filter(name -> !this.activityRepository.existsByName(name))
                    .map(name -> Activity.builder()
                            .name(name)
                            .type(ActivityType.PARTNER)
                            .build())
                    .collect(Collectors.toList());

            this.activityRepository.saveAll(activities);

            if (!this.activityRepository.existsByName("dar diafa")) {
                Activity activity2 = Activity.builder()
                        .name("dar diafa")
                        .type(ActivityType.ASSOCIATION) // Set the type here
                        .build();
                this.activityRepository.save(activity2);
            }

            if (!this.activityRepository.existsByName("banque alimentaire")) {
                Activity activity4 = Activity.builder()
                        .name("banque alimentaire")
                        .type(ActivityType.ASSOCIATION) // Set the type here
                        .build();
                this.activityRepository.save(activity4);
            }

            if (!this.activityRepository.existsByName("livraison")) {
                Activity activity3 = Activity.builder()
                        .name("livraison")
                        .type(ActivityType.DELIVERY_PARTNER) // Set the type here
                        .build();
                this.activityRepository.save(activity3);
            }
        }
    }
}
