package net.foodeals.location.infrastructure.interfaces.web;

import jakarta.validation.Valid;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.StateResponse;
import net.foodeals.location.application.services.StateService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/states")
@RequiredArgsConstructor
public class StateController {
    private final StateService service;
    private final ModelMapper mapper;

    @GetMapping
    @Transactional
    public ResponseEntity<List<StateResponse>> getAll(Pageable pageable) {
        final List<StateResponse> responses = service.findAll(pageable)
                .stream()
                .map(state -> mapper.map(state, StateResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<StateResponse> getById(@PathVariable("id") UUID id) {
        final StateResponse response = mapper.map(service.findById(id), StateResponse.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<StateResponse> create(@RequestBody @Valid StateRequest request) {
        final StateResponse response = mapper.map(service.create(request), StateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<StateResponse> update(@PathVariable UUID id, @RequestBody @Valid StateRequest request) {
        final StateResponse response = mapper.map(service.update(id, request), StateResponse.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cities")
    @Transactional
    public ResponseEntity<List<CityResponse>> getCities(@PathVariable("id") UUID id) {
        final List<CityResponse> responses = service.getCities(id)
                .stream()
                .map(city -> mapper.map(city, CityResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }
}