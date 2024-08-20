package net.foodeals.user.infrastructure.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.user.application.dtos.requests.AuthorityRequest;
import net.foodeals.user.application.dtos.responses.AuthorityResponse;
import net.foodeals.user.application.services.AuthorityService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/authorities")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService service;

    @GetMapping
    public ResponseEntity<List<AuthorityResponse>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public ResponseEntity<Page<AuthorityResponse>> getAll(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        return ResponseEntity.ok(service.findAll(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorityResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorityResponse> create(@RequestBody @Valid AuthorityRequest request) {
        final AuthorityResponse authority = service.create(request);
        return new ResponseEntity<>(authority, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuthorityResponse> update(@PathVariable UUID id, @RequestBody @Valid AuthorityRequest request) {
        final AuthorityResponse authority = service.update(id, request);
        return ResponseEntity.ok(authority);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
