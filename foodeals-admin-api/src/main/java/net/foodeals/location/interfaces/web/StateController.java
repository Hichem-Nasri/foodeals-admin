package net.foodeals.location.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.StateRequest;
import net.foodeals.location.application.services.StateService;
import net.foodeals.location.domain.entities.State;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/states")
@RequiredArgsConstructor
public class StateController {
    private final StateService service;

    @GetMapping
    public ResponseEntity<List<State>> getAll(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        final Page<State> states = service.findAll(pageNum, pageSize);
        return ResponseEntity.ok(states.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<State> getById(@PathVariable("id") UUID id) {
        final State state = service.findById(id);
        return ResponseEntity.ok(state);
    }

    @PostMapping
    public ResponseEntity<State> create(@RequestBody @Valid StateRequest request) {
        final State savedState = service.create(request);
        return new ResponseEntity<>(savedState, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<State> update(@PathVariable UUID id, @RequestBody @Valid StateRequest request) {
        final State updatedState = service.update(id, request);
        return ResponseEntity.ok(updatedState);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
