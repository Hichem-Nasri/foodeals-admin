package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.organizationEntity.domain.entities.Features;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeatureRepository extends BaseRepository<Features, UUID> {
    Set<Features> findByNameIn(List<String> features);

    boolean existsByName(String name);
}

