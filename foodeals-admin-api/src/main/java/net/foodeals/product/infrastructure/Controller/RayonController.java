package net.foodeals.product.infrastructure.Controller;

import net.foodeals.product.application.dtos.requests.CreateRayonDto;
import net.foodeals.product.application.dtos.responses.UpdateRayonDto;
import net.foodeals.product.application.services.impl.RayonService;
import net.foodeals.product.domain.entities.Rayon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class RayonController {

    private final RayonService rayonService;


    public RayonController(RayonService rayonService) {
        this.rayonService = rayonService;
    }

    @GetMapping("Rayon")
    public ResponseEntity<List<Rayon>> getRayon() {
        return new ResponseEntity<List<Rayon>>(this.rayonService.getRayon(), HttpStatus.OK);
    }

    @GetMapping("Rayon/{id}")
    public ResponseEntity<Rayon> getRayonById(@PathVariable("id") UUID id) {
        return new ResponseEntity<Rayon>(this.rayonService.findRayonById(id), HttpStatus.OK);
    }

    @PostMapping("Rayon")
    public ResponseEntity<Rayon> createRayon(@RequestBody CreateRayonDto createRayonDto) {
        return new ResponseEntity<Rayon>(this.rayonService.createRayon(createRayonDto), HttpStatus.CREATED);
    }

    @DeleteMapping("Rayon/{id}")
    public ResponseEntity<String> deleteRayon(@PathVariable("id") UUID id) {
        return new ResponseEntity<String>(this.rayonService.deleteRayon(id), HttpStatus.OK);
    }

    @PutMapping("Rayon/{id}")
    public ResponseEntity<Rayon> deleteRayon(@PathVariable("id") UUID id,@RequestBody UpdateRayonDto updateRayonDto) {
        return new ResponseEntity<Rayon>(this.rayonService.updateRayon(id, updateRayonDto), HttpStatus.OK);
    }
}
