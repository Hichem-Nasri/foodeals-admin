package net.foodeals.user.domain.repositories;

import net.foodeals.user.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
