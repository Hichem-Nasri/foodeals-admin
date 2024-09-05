package net.foodeals.contract.domain.repositories;

import net.foodeals.contract.domain.entities.UserContract;
import net.foodeals.user.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserContractRepository extends JpaRepository<UserContract, UUID> {
}
