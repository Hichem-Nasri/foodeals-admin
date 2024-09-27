package net.foodeals.offer.application.services.impl;

import lombok.RequiredArgsConstructor;
import net.foodeals.offer.application.dtos.requests.DonationRequest;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.offer.domain.repositories.DonationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationServiceImp implements DonationService {

    private final DonationRepository donationRepository;

    @Override
    public List<Donation> findAll() {
        return List.of();
    }

    @Override
    public Page<Donation> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Donation findById(UUID uuid) {
        return null;
    }

    @Override
    public Donation create(DonationRequest dto) {
        return null;
    }

    @Override
    public Donation update(UUID uuid, DonationRequest dto) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {

    }

    @Override
    public Integer countByDonor_Id(UUID id) {
        return this.donationRepository.countByDonor_Id(id);
    }

    @Override
    public Integer countByReceiver_Id(UUID id) {
        return this.donationRepository.countByReceiver_Id(id);
    }

    @Override
    public List<Donation> saveAll(List<Donation> donations) {
        return this.donationRepository.saveAll(donations);
    }

    @Override
    public Long count() {
        return this.donationRepository.count();
    }
}
