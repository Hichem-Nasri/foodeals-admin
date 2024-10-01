package net.foodeals.crm.infrastructure.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.ProspectPartialRequest;
import net.foodeals.crm.application.dto.requests.ProspectRequest;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.services.ProspectService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm")
@AllArgsConstructor
public final class ProspectController {

    private final ProspectService prospectService;
    private final ModelMapper modelMapper;

    @PostMapping("/prospects/create")
    public ResponseEntity<ProspectResponse> create(@RequestBody @Valid ProspectRequest prospectRequest) {
        return new ResponseEntity<ProspectResponse>(this.prospectService.create(prospectRequest), HttpStatus.CREATED);
    }

    @GetMapping("/prospects")
    public ResponseEntity<Page<ProspectResponse>> getAll(Pageable pageable) {
        return new ResponseEntity<Page<ProspectResponse>>(this.prospectService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(this.prospectService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody ProspectRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.update(id, prospectRequest), HttpStatus.OK);
    }

    @PatchMapping("/prospects/{id}")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @RequestBody ProspectPartialRequest prospectRequest) {
        return new ResponseEntity<>(this.prospectService.partialUpdate(id, prospectRequest), HttpStatus.OK);
    }


    @DeleteMapping("/prospects/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.prospectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
