package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
