package net.foodeals.offer.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.offer.application.dtos.requests.DonationRequest;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.offer.domain.enums.DonorType;

import java.util.List;
import java.util.UUID;

public interface DonationService extends CrudService<Donation, UUID, DonationRequest> {
    Integer countByDonor_Id(UUID type);
    Integer countByReceiver_Id(UUID type);
    List<Donation> saveAll(List<Donation> donations);

    Long count();
}
