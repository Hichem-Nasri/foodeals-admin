package net.foodeals.location.infrastructure.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.CountryResponse;
import net.foodeals.location.application.dtos.responses.StateResponse;
import net.foodeals.location.application.services.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService service;
    private final ModelMapper mapper;

    @GetMapping
    @Transactional
    public ResponseEntity<List<CountryResponse>> getAll(Pageable pageable) {
        final List<CountryResponse> responses = service.findAll(pageable)
                .stream()
                .map(country -> mapper.map(country, CountryResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<CountryResponse> getById(@PathVariable("id") UUID id) {
        final CountryResponse response = mapper.map(service.findById(id), CountryResponse.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CountryResponse> create(@RequestBody @Valid CountryRequest request) {
        final CountryResponse response = mapper.map(service.create(request), CountryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<CountryResponse> update(@PathVariable UUID id, @RequestBody @Valid CountryRequest request) {
        final CountryResponse response = mapper.map(service.update(id, request), CountryResponse.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/states")
    @Transactional
    public ResponseEntity<List<StateResponse>> getStates(@PathVariable("id") UUID id) {
        final List<StateResponse> responses = service.getStates(id)
                .stream()
                .map(state -> mapper.map(state, StateResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
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