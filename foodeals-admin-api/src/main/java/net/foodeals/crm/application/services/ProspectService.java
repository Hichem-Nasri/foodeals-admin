package net.foodeals.crm.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.crm.application.dto.requests.*;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.dto.responses.ProspectStatisticDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProspectService extends CrudService<ProspectResponse, UUID, ProspectRequest> {
    Page<ProspectResponse> findAll(Pageable pageable);

    ProspectResponse partialUpdate(UUID id, PartialProspectRequest dto);

    EventResponse createEvent(UUID id, EventRequest eventRequest);

    Page<EventResponse> getEvents(UUID id, Pageable pageable);

    EventResponse getEventById(UUID prospectId, UUID eventId);

    EventResponse updateEvent(UUID prospectId, UUID eventId, EventRequest dto);

    EventResponse partialUpdateEvent(UUID prospectId, UUID eventId, PartialEventRequest eventRequest);

    void deleteEvent(UUID prospectId, UUID eventId);

    ProspectStatisticDto statistics();

    String changeStatus(UUID id, ProspectStatusRequest status);
}
