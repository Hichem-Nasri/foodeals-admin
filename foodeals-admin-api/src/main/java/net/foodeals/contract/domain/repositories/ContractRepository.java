package net.foodeals.contract.domain.repositories;

import net.foodeals.contract.domain.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
