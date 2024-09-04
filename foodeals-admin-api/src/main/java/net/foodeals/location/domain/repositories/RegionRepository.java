package net.foodeals.location.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.location.domain.entities.Region;

import java.util.UUID;

public interface RegionRepository extends BaseRepository<Region, UUID> {
    Region findByName(String name);
}
