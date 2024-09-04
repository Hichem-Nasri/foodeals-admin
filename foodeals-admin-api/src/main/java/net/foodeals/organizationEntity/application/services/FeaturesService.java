package net.foodeals.organizationEntity.application.services;

import net.foodeals.organizationEntity.domain.entities.Features;
import net.foodeals.organizationEntity.domain.repositories.FeatureRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FeaturesService {

    private final FeatureRepository featureRepository;


    public FeaturesService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public Set<Features> findFeaturesByNames(List<String> features) {
        return this.featureRepository.findByNameIn(features);
    }

    public Features save(Features feature) {
        return this.featureRepository.save(feature);
    }
}
