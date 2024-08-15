package net.foodeals.user.domain.repositories;

import net.foodeals.user.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Permission, UUID> {
}
