package net.foodeals.crm.infrastructure.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.crm.application.dto.requests.*;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.application.dto.responses.ProspectFilter;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.dto.responses.ProspectStatisticDto;
import net.foodeals.crm.application.services.ProspectService;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/crm")
@AllArgsConstructor
public class ProspectController {

    private final ProspectService prospectService;
    private final ModelMapper modelMapper;

    @Transactional
    @PostMapping("/prospects/create")
    public ResponseEntity<ProspectResponse> create(@RequestBody @Valid ProspectRequest prospectRequest) {
        return new ResponseEntity<ProspectResponse>(this.prospectService.create(prospectRequest), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(this.prospectService.findById(id), HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody ProspectRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.update(id, prospectRequest), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/prospects/search")
    public ResponseEntity<Page<PartnerInfoDto>> searchProspects(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "types", required = false) List<ProspectType> types,
            @RequestParam(name = "deleted", required = false, defaultValue = "false") boolean includeDeleted,
            Pageable pageable) {
        return ResponseEntity.ok(
                prospectService.searchProspectsByName(name, types, pageable, includeDeleted)
                        .map(p -> this.modelMapper.map(p, PartnerInfoDto.class))
        );
    }

    @Transactional
    @GetMapping("/prospects")
    public ResponseEntity<Page<ProspectResponse>> getAll(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "names", required = false) String names,
            @RequestParam(value = "categories", required = false) List<String> categories,
            @RequestParam(value = "cityId", required = false) UUID cityId,
            @RequestParam(value = "countryId", required = false) UUID countryId,
            @RequestParam(value = "creatorId", required = false) Integer creatorId,
            @RequestParam(value = "leadId", required = false) Integer leadId,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "statuses", required = false) List<ProspectStatus> statuses,
            @RequestParam(value = "types", required = false) List<ProspectType> types,
            Pageable pageable) {

        List<String> namesList = names != null
                ? Arrays.stream(names.split(","))
                .collect(Collectors.toList())
                : null;


        ProspectFilter filter = ProspectFilter.builder()
                .startDate(startDate != null ?  startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null)
                .endDate(endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null)
                .names(namesList)
                .categories(categories != null ? categories : new ArrayList<String>())
                .cityId(cityId)
                .countryId(countryId)
                .creatorId(creatorId)
                .leadId(leadId)
                .types(types)
                .email(email)
                .phone(phone)
                .statuses(statuses)
                .build();

        return new ResponseEntity<>(this.prospectService.findAllWithFilters(filter, pageable), HttpStatus.OK);
    }

    @Transactional
    @PatchMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody PartialProspectRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.partialUpdate(id, prospectRequest), HttpStatus.OK);
    }


    @Transactional
    @DeleteMapping("/prospects/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.prospectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/prospects/{id}/events/create")
    public ResponseEntity<EventResponse> createEvent(@PathVariable("id") UUID id, @RequestBody EventRequest eventRequest) {
        return new ResponseEntity<>(this.prospectService.createEvent(id, eventRequest), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping("/prospects/{id}/events")
    public ResponseEntity<Page<EventResponse>> getEvents(@PathVariable("id") UUID id, Pageable pageable) {
        return new ResponseEntity<Page<EventResponse>>(this.prospectService.getEvents(id, pageable), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId) {
        return new ResponseEntity<EventResponse>(this.prospectService.getEventById(prospectId, eventId), HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId, @RequestBody EventRequest eventRequest) {
        return new ResponseEntity<EventResponse>(this.prospectService.updateEvent(prospectId, eventId, eventRequest), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/prospects/status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") UUID id, @RequestBody ProspectStatusRequest status) {
        System.out.println(status);
        return new ResponseEntity<String>(this.prospectService.changeStatus(id, status), HttpStatus.OK);
    }

    @Transactional
    @PatchMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<EventResponse> partialUpdateEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId, @RequestBody PartialEventRequest eventRequest) {
        return new ResponseEntity<EventResponse>(this.prospectService.partialUpdateEvent(prospectId, eventId, eventRequest), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/prospects/{prospectId}/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("prospectId") UUID prospectId,@PathVariable("eventId") UUID eventId) {
        this.prospectService.deleteEvent(prospectId, eventId);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @GetMapping("/prospects/{uuid}/deletion-details")
    public ResponseEntity<Page<UpdateDetails>> getDeletionDetails(@PathVariable UUID uuid, Pageable pageable) {
        Page<UpdateDetails> deletionDetails = prospectService.getDeletionDetails(uuid, pageable);
        return ResponseEntity.ok(deletionDetails);
    }

    @Transactional
    @GetMapping("/prospects/statistics")
    public ResponseEntity<ProspectStatisticDto> statistics(@RequestParam(value = "types", required = true) List<ProspectType> type) {
        return new ResponseEntity<ProspectStatisticDto>(this.prospectService.statistics(type), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("prospects/cities/search")
    public ResponseEntity<Page<CityResponse>> searchCities(
            @RequestParam(name = "city") String cityName,
            @RequestParam(name = "types", required = true) List<ProspectType> types,
            @RequestParam(name = "country") String countryName,
            Pageable pageable) {
        return ResponseEntity.ok(prospectService.searchCitiesByProspectAddress(types, cityName, countryName, pageable).map( c ->this.modelMapper.map(c, CityResponse.class)));
    }

    @Transactional
    @GetMapping("prospects/regions/search")
    public ResponseEntity<Page<RegionResponse>> searchRegions(
            @RequestParam(name = "region") String regionName,
            @RequestParam(name = "types", required = true) List<ProspectType> types,
            @RequestParam(name = "country") String countryName,
            Pageable pageable) {
        return ResponseEntity.ok(prospectService.searchRegionsByProspectAddress(types, regionName, countryName, pageable).map(c -> this.modelMapper.map(c, RegionResponse.class)));
    }
}