package net.foodeals.crm.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.crm.application.dto.requests.*;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.application.dto.responses.ProspectFilter;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.dto.responses.ProspectStatisticDto;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
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

    ProspectStatisticDto statistics(List<ProspectType> type);

    String changeStatus(UUID id, ProspectStatusRequest status);
    Page<ProspectResponse> findAllWithFilters(ProspectFilter filter, Pageable pageable);

    Page<UpdateDetails> getDeletionDetails(UUID uuid, Pageable pageable);

    Page<City> searchCitiesByProspectAddress(List<ProspectType> types, String cityName, String countryName, Pageable pageable);
    Page<Region> searchRegionsByProspectAddress(List<ProspectType> types, String regionName, String countryName, Pageable pageable);
}
