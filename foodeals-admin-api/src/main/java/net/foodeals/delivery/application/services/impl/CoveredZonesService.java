package net.foodeals.delivery.application.services.impl;

import jakarta.transaction.Transactional;
import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.delivery.domain.repositories.CoveredZonesRepository;
import org.springframework.stereotype.Service;

@Service
public class CoveredZonesService {

    private final CoveredZonesRepository coveredZonesRepository;


    public CoveredZonesService(CoveredZonesRepository coveredZonesRepository) {
        this.coveredZonesRepository = coveredZonesRepository;
    }

    @Transactional
    public CoveredZones save(CoveredZones coveredZones) {
        return this.coveredZonesRepository.save(coveredZones);
    }
}
