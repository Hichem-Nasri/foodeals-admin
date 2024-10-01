package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.application.services.FeaturesService;
import net.foodeals.organizationEntity.domain.entities.Features;
import net.foodeals.organizationEntity.domain.repositories.FeatureRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeatureSeeder {

    private final FeatureRepository featureRepository;

    public FeatureSeeder(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void featureSeeder() {
        if (this.featureRepository.count() == 0) {
            Features feature = Features.builder().name("f1").build();
            Features f2 = Features.builder().name("f2").build();
            Features f3 = Features.builder().name("f3").build();
            List<Features> featuresList = new ArrayList<>();
            featuresList.add(feature);
            featuresList.add(f2);
            featuresList.add(f3);

            this.featureRepository.saveAll(featuresList);
        }
    }
}
