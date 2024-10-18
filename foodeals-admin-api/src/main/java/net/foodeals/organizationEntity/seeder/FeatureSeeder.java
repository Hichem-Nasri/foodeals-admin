package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
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
        if (!this.featureRepository.existsByName("seller_pro")) {
            Features features = Features.builder().name("seller_pro").build();
            this.featureRepository.save(features);
        }
        if (!this.featureRepository.existsByName("buyer_pro")) {
            Features f2 = Features.builder().name("buyer_pro").build();
            this.featureRepository.save(f2);
        }
    }
}
