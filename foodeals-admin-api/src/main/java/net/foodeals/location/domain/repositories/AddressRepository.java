package net.foodeals.location.domain.repositories;

import net.foodeals.location.domain.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
