package net.foodeals.location.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.CountryRequest;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.Country;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService service;

    @GetMapping
    public ResponseEntity<List<Country>> getAll(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        final Page<Country> countries = service.findAll(pageNum, pageSize);
        return ResponseEntity.ok(countries.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getById(@PathVariable("id") UUID id) {
        final Country country = service.findById(id);
        return ResponseEntity.ok(country);
    }

    @PostMapping
    public ResponseEntity<Country> create(@RequestBody @Valid CountryRequest request) {
        final Country savedCountry = service.create(request);
        return new ResponseEntity<>(savedCountry, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Country> update(@PathVariable UUID id, @RequestBody @Valid CountryRequest request) {
        final Country updatedCountry = service.update(id, request);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
