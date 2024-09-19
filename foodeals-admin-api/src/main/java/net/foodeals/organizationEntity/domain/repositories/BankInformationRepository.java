package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.organizationEntity.domain.entities.BankInformation;

import java.util.UUID;

public interface BankInformationRepository extends BaseRepository<BankInformation, UUID> {
}
