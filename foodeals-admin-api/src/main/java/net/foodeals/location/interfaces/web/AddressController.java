package net.foodeals.location.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.domain.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService service;

    @GetMapping
    public ResponseEntity<List<Address>> getAll(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        final Page<Address> addresses = service.findAll(pageNum, pageSize);
        return ResponseEntity.ok(addresses.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getById(@PathVariable("id") UUID id) {
        final Address address = service.findById(id);
        return ResponseEntity.ok(address);
    }

    @PostMapping
    public ResponseEntity<Address> create(@RequestBody @Valid AddressRequest request) {
        final Address savedAddress = service.create(request);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Address> update(@PathVariable UUID id, @RequestBody @Valid AddressRequest request) {
        final Address updatedAddress = service.update(id, request);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
