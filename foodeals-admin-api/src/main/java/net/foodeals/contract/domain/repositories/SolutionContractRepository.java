package net.foodeals.contract.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.contract.domain.entities.SolutionContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SolutionContractRepository extends BaseRepository<SolutionContract, UUID> {
}
