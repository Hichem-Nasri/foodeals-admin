package net.foodeals.crm.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.crm.domain.entities.Event;
import net.foodeals.crm.domain.entities.Prospect;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends BaseRepository<Event, UUID> {
}
