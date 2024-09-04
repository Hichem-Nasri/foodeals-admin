package net.foodeals.product.application.services.impl;

import net.foodeals.product.application.dtos.requests.CreateRayonDto;
import net.foodeals.product.application.dtos.responses.UpdateRayonDto;
import net.foodeals.product.domain.entities.Rayon;
import net.foodeals.product.domain.repositories.RayonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RayonService {

    private final RayonRepository rayonRepository;


    public RayonService(RayonRepository rayonRepository) {
        this.rayonRepository = rayonRepository;
    }

    public List<Rayon> getRayon() {
        return this.rayonRepository.findAll();
    }

    public Rayon findRayonById(UUID id) {
        Rayon rayon = this.rayonRepository.findById(id).orElse(null);

        if (rayon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rayon not found");
        }
        return rayon;
    }

    public Rayon createRayon(CreateRayonDto createRayonDto) {
        Rayon rayon = Rayon.builder().name(createRayonDto.getName()).build();
        return this.rayonRepository.save(rayon);
    }

    public Rayon updateRayon(UUID id, UpdateRayonDto updateRayonDto) {
        Rayon rayon = this.rayonRepository.findById(id).orElse(null);

        if (rayon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rayon not found");
        }

        rayon.setName(updateRayonDto.getName());
        return this.rayonRepository.save(rayon);
    }

    public String deleteRayon(UUID id) {
        Rayon rayon = this.rayonRepository.findById(id).orElse(null);

        if (rayon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rayon not found");
        }
        this.rayonRepository.delete(rayon);
        return "Rayon has been deleted";
    }
}
