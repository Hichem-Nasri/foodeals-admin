package net.foodeals.crm.infrastructure.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.*;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.dto.responses.ProspectStatisticDto;
import net.foodeals.crm.application.services.ProspectService;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm")
@AllArgsConstructor
public final class ProspectController {

    private final ProspectService prospectService;
    private final ModelMapper modelMapper;

    @PostMapping("/prospects/create")
    public ResponseEntity<ProspectResponse> create(@RequestBody @Valid ProspectRequest prospectRequest) {
        return new ResponseEntity<ProspectResponse>(this.prospectService.create(prospectRequest), HttpStatus.CREATED);
    }

    @GetMapping("/prospects")
    public ResponseEntity<Page<ProspectResponse>> getAll(Pageable pageable) {
        return new ResponseEntity<Page<ProspectResponse>>(this.prospectService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(this.prospectService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody ProspectRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.update(id, prospectRequest), HttpStatus.OK);
    }

    @PatchMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody PartialProspectRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.partialUpdate(id, prospectRequest), HttpStatus.OK);
    }


    @DeleteMapping("/prospects/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.prospectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prospects/{id}/events/create")
    public ResponseEntity<EventResponse> createEvent(@PathVariable("id") UUID id, @RequestBody EventRequest eventRequest) {
        return new ResponseEntity<>(this.prospectService.createEvent(id, eventRequest), HttpStatus.CREATED);
    }

    @GetMapping("/prospects/{id}/events")
    public ResponseEntity<Page<EventResponse>> getEvents(@PathVariable("id") UUID id, Pageable pageable) {
        return new ResponseEntity<Page<EventResponse>>(this.prospectService.getEvents(id, pageable), HttpStatus.OK);
    }

    @GetMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId) {
        return new ResponseEntity<EventResponse>(this.prospectService.getEventById(prospectId, eventId), HttpStatus.OK);
    }

    @PutMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId, @RequestBody EventRequest eventRequest) {
        return new ResponseEntity<EventResponse>(this.prospectService.updateEvent(prospectId, eventId, eventRequest), HttpStatus.OK);
    }

    @PostMapping("/prospects/status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") UUID id, @RequestBody ProspectStatusRequest status) {
        System.out.println(status);
        return new ResponseEntity<String>(this.prospectService.changeStatus(id, status), HttpStatus.OK);
    }

    @PatchMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> partialUpdateEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId, @RequestBody PartialEventRequest eventRequest) {
        return new ResponseEntity<EventResponse>(this.prospectService.partialUpdateEvent(prospectId, eventId, eventRequest), HttpStatus.OK);
    }

    @DeleteMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId) {
        this.prospectService.deleteEvent(prospectId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/prospects/statistics")
    public ResponseEntity<ProspectStatisticDto> statistics() {
        return new ResponseEntity<ProspectStatisticDto>(this.prospectService.statistics(), HttpStatus.OK);
    }
}
