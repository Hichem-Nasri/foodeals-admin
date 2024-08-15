package net.foodeals.contract.domain.repositories;

import net.foodeals.contract.domain.entities.SolutionContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionContractRepository extends JpaRepository<SolutionContract, Long> {
}
