package net.foodeals.contract.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends BaseRepository<Contract, UUID> {
    List<Contract> findByContractStatus(ContractStatus status);
}
