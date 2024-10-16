package net.foodeals.organizationEntity.application.services.impl;

import net.foodeals.organizationEntity.application.dtos.requests.FeatureRequest;
import net.foodeals.organizationEntity.application.services.FeatureService;
import net.foodeals.organizationEntity.domain.entities.Features;
import net.foodeals.organizationEntity.domain.repositories.FeatureRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeaturesServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;


    public FeaturesServiceImpl(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public Set<Features> findFeaturesByNames(List<String> features) {
        return this.featureRepository.findByNameIn(features.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }
    @Override
    public Features save(Features feature) {
        return this.featureRepository.save(feature);
    }

    @Override
    public List<Features> findAll() {
        return List.of();
    }

    @Override
    public Page<Features> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<Features> findAll(Pageable pageable) {
        return this.featureRepository.findAll(pageable);
    }

    @Override
    public Features findById(UUID uuid) {
        return this.featureRepository.findById(uuid).orElse(null);
    }

    @Override
    public Features create(FeatureRequest dto) {
        Features feature = Features.builder().name(dto.name().toLowerCase()).build();
        return this.featureRepository.save(feature);    }

    @Override
    public Features update(UUID uuid, FeatureRequest dto) {
        Features feature = featureRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found with id " + uuid.toString()));

        feature.setName(dto.name().toLowerCase());
        return this.featureRepository.save(feature);
    }

    @Override
    public void delete(UUID uuid) {
        Features feature = featureRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found with id " + uuid.toString()));

        this.featureRepository.delete(feature);
    }
}
