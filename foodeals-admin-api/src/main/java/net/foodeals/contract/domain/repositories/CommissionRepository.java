package net.foodeals.contract.domain.repositories;

import net.foodeals.contract.domain.entities.Commission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
}
