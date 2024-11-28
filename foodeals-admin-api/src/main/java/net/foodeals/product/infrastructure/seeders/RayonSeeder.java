package net.foodeals.product.infrastructure.seeders;

import net.foodeals.product.application.dtos.requests.CreateRayonDto;
import net.foodeals.product.application.services.impl.RayonService;
import net.foodeals.product.domain.entities.Rayon;
import net.foodeals.product.domain.repositories.RayonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RayonSeeder {

    private final RayonRepository rayonRepository;
    private final RayonService rayonService;

    @Autowired
    public RayonSeeder(RayonRepository rayonRepository, RayonService rayonService) {
        this.rayonRepository = rayonRepository;
        this.rayonService = rayonService;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        if (rayonRepository.count() == 0) {
            List<String> rayonNames = Arrays.asList("fromagerie", "boulangerie", "patisserie", "charcuterie");
            rayonNames.forEach(rayonName -> rayonService.createRayon(new CreateRayonDto(rayonName)));
        }
    }
}
