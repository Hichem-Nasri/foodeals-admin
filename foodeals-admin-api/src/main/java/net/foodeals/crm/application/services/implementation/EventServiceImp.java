package net.foodeals.crm.application.services.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.EventRequest;
import net.foodeals.crm.application.dto.requests.PartialEventRequest;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.domain.entities.Event;
import net.foodeals.crm.application.services.EventService;
import net.foodeals.crm.domain.repositories.EventRepository;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public final class EventServiceImp implements EventService {

    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Event partialUpdate(UUID id, PartialEventRequest dto) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id.toString()));

        if (dto.dateAndTime() != null) {
            event.setDateAndHour(dto.dateAndTime());
        }
        if (dto.object() != null) {
            event.setObject(dto.object());
        }
        if (dto.message() != null) {
            event.setMessage(dto.message());
        }
        if (dto.lead() != null) {
            if (event.getLead().getId() != dto.lead()) {
                User newlead = this.userService.findById(dto.lead());
                event.getLead().getEvents().remove(event);
                this.userService.save(event.getLead());
                if (newlead.getEvents() == null) {
                    newlead.setEvents(new ArrayList<>(List.of(event)));
                } else {
                    newlead.getEvents().add(event);
                }
                event.setLead(newlead);
                this.userService.save(newlead);
            }
        }
        return this.eventRepository.save(event);
    }


    @Override
    public List<Event> findAll() {
        return List.of();
    }

    @Override
    public Page<Event> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Event findById(UUID uuid) {
        return null;
    }

    @Override
    public Event create(EventRequest dto) {
        User lead = this.userService.findById(dto.lead());
        Event event = Event.builder()
                .lead(lead)
                .dateAndHour(dto.dateAndTime())
                .object(dto.object())
                .message(dto.message())
                .build();
        if (lead.getEvents() == null) {
            lead.setEvents(new ArrayList<>(List.of(event)));
        } else {
            lead.getEvents().add(event);
        }
        eventRepository.save(event);
        userService.save(lead);
        return event;
    }

    @Override
    public Event update(UUID id, EventRequest dto) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id.toString()));
        event.setDateAndHour(dto.dateAndTime());
        event.setObject(dto.object());
        event.setMessage(dto.message());

        if (event.getLead().getId() != dto.lead()) {
            User newlead = this.userService.findById(dto.lead());
            event.getLead().getEvents().remove(event);
            this.userService.save(event.getLead());
            if (newlead.getEvents() == null) {
                newlead.setEvents(new ArrayList<>(List.of(event)));
            } else {
                newlead.getEvents().add(event);
            }
            event.setLead(newlead);
            this.userService.save(newlead);
        }
        return this.eventRepository.save(event);
    }

    @Override
    public void delete(UUID id) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id.toString()));
        this.eventRepository.softDelete(event.getId());
    }
}
