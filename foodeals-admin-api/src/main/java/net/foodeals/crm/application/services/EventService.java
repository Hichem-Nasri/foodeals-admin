package net.foodeals.crm.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.crm.application.dto.requests.EventRequest;
import net.foodeals.crm.application.dto.requests.PartialEventRequest;
import net.foodeals.crm.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventService extends CrudService<Event, UUID, EventRequest> {
    Page<Event> findAll(Pageable pageable);
    Event partialUpdate(UUID id, PartialEventRequest dto);
}
