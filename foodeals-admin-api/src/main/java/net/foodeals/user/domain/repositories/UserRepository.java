package net.foodeals.user.domain.repositories;

import net.foodeals.user.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
