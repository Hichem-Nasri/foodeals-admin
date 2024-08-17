package net.foodeals.user.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.user.domain.entities.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
