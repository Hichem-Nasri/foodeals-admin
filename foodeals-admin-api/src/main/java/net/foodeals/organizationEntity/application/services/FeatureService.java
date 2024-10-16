package net.foodeals.organizationEntity.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.organizationEntity.application.dtos.requests.FeatureRequest;
import net.foodeals.organizationEntity.application.dtos.responses.FeatureResponse;
import net.foodeals.organizationEntity.domain.entities.Features;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeatureService extends CrudService<Features, UUID, FeatureRequest> {
    Set<Features> findFeaturesByNames(List<String> features);
    Features save(Features feature);
}
