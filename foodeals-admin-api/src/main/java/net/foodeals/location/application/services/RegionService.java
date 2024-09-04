package net.foodeals.location.application.services;

import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.repositories.RegionRepository;
import org.springframework.stereotype.Service;

@Service
public class RegionService {
    private final RegionRepository regionRepository;


    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public Region findByName(String name) {
        return this.regionRepository.findByName(name);
    }
}
