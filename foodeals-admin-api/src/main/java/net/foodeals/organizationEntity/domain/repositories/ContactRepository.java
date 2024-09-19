package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.organizationEntity.domain.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends BaseRepository<Contact, UUID> {
    Contact findByEmail(String email);
}
