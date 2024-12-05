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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventServiceImp implements EventService {

    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
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
    @Transactional
    public List<Event> findAll() {
        return List.of();
    }

    @Override
    @Transactional
    public Page<Event> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    @Transactional
    public Event findById(UUID uuid) {
        return null;
    }

    @Override
    @Transactional
    public Event create(EventRequest dto) {

        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        final User creator = this.userService.findByEmail(email);

        Event event = Event.builder()
                .lead(creator)
                .dateAndHour(dto.dateAndTime())
                .object(dto.object())
                .message(dto.message())
                .build();
        if (creator.getEvents() == null) {
            creator.setEvents(new ArrayList<>(List.of(event)));
        } else {
            creator.getEvents().add(event);
        }
        eventRepository.save(event);
        userService.save(creator);
        return event;
    }

    @Override
    @Transactional
    public Event update(UUID id, EventRequest dto) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id.toString()));
        event.setDateAndHour(dto.dateAndTime());
        event.setObject(dto.object());
        event.setMessage(dto.message());


        final String email = SecurityContextHolder.getContext().getAuthentication().getName();

        final User newlead = this.userService.findByEmail(email);

        if (event.getLead().getId() != newlead.getId()) {
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
    @Transactional
    public void delete(UUID id) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id.toString()));
        this.eventRepository.softDelete(event.getId());
    }
}