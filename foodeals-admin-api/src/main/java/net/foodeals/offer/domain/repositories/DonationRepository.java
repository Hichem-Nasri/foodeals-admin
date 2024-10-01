package net.foodeals.offer.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.offer.application.dtos.requests.DonationRequest;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.offer.domain.enums.DonorType;

import java.util.UUID;

public interface DonationRepository extends BaseRepository<Donation, UUID> {
    Integer countByDonor_Id(UUID id);
    Integer countByReceiver_Id(UUID id);
}