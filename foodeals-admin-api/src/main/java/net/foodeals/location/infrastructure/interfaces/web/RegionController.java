package net.foodeals.location.infrastructure.interfaces.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.foodeals.location.application.dtos.requests.RegionRequest;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.application.services.impl.RegionServiceImpl;
import net.foodeals.location.domain.entities.Region;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/regions")
@AllArgsConstructor
@RestController
public class RegionController {

    private final RegionService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<List<RegionResponse>> getAll(Pageable pageable
    ) {
        final List<RegionResponse> responses = service.findAll(pageable)
                .stream()
                .map((Region region) -> mapper.map(region, RegionResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionResponse> getById(@PathVariable("id") UUID id) {
        final RegionResponse response = mapper.map(service.findById(id), RegionResponse.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RegionResponse> create(@RequestBody @Valid RegionRequest request) {
        final RegionResponse response = mapper.map(service.create(request), RegionResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionResponse> update(@PathVariable UUID id, @RequestBody @Valid RegionRequest request) {
        final RegionResponse response = mapper.map(service.update(id, request), RegionResponse.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
