package net.foodeals.location.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CityRequest;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.domain.entities.City;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService service;

    @GetMapping
    public ResponseEntity<List<City>> getAll(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        final Page<City> cities = service.findAll(pageNum, pageSize);
        return ResponseEntity.ok(cities.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getById(@PathVariable("id") UUID id) {
        final City city = service.findById(id);
        return ResponseEntity.ok(city);
    }

    @PostMapping
    public ResponseEntity<City> create(@RequestBody @Valid CityRequest request) {
        final City savedCity = service.create(request);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<City> update(@PathVariable UUID id, @RequestBody @Valid CityRequest request) {
        final City updatedCity = service.update(id, request);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
