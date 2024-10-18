package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.domain.entities.Region;

import java.util.UUID;

public interface RegionService extends CrudService<Region, UUID, RegionRequest> {
    Long count();

    boolean existsByName(String region);
}
