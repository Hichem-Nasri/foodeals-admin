package net.foodeals.user.domain.repositories;

import net.foodeals.user.domain.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
