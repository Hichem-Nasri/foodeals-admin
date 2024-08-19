package net.foodeals.user.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.user.application.dtos.requests.AuthorityRequest;
import net.foodeals.user.application.services.AuthorityService;
import net.foodeals.user.domain.entities.Authority;
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
    public ResponseEntity<List<Authority>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public ResponseEntity<Page<Authority>> getAll(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        return ResponseEntity.ok(service.findAll(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Authority> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Authority> create(@RequestBody @Valid AuthorityRequest request) {
        final Authority authority = service.create(request);
        return new ResponseEntity<>(authority, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Authority> update(@PathVariable UUID id, @RequestBody @Valid AuthorityRequest request) {
        final Authority authority = service.update(id, request);
        return ResponseEntity.ok(authority);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
