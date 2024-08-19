package net.foodeals.user.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.user.application.dtos.requests.RoleRequest;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.domain.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @GetMapping
    public ResponseEntity<List<Role>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public ResponseEntity<Page<Role>> getAll(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        return ResponseEntity.ok(service.findAll(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getByName(@PathVariable String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody @Valid RoleRequest request) {
        final Role role = service.create(request);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable UUID id, @RequestBody @Valid RoleRequest request) {
        final Role role = service.update(id, request);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
