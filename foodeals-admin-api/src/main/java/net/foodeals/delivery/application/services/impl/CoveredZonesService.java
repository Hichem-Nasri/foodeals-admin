package net.foodeals.delivery.application.services.impl;

import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.delivery.domain.repositories.CoveredZonesRepository;
import org.springframework.stereotype.Service;

@Service
public class CoveredZonesService {

    private final CoveredZonesRepository coveredZonesRepository;


    public CoveredZonesService(CoveredZonesRepository coveredZonesRepository) {
        this.coveredZonesRepository = coveredZonesRepository;
    }


    public CoveredZones save(CoveredZones coveredZones) {
        return this.coveredZonesRepository.save(coveredZones);
    }
}
