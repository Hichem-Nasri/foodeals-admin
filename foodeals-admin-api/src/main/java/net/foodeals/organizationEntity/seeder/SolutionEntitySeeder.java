package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.repositories.ActivityRepository;
import net.foodeals.organizationEntity.domain.repositories.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6)
public class SolutionEntitySeeder {

    @Autowired
    private final SolutionRepository solutionRepository;

    public SolutionEntitySeeder(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void SolutionSeeder() {
        if (!this.solutionRepository.existsByName("pro_market")) {
            Solution solution = Solution.builder()
                    .name("pro_market")
                    .build();
            this.solutionRepository.save(solution);
        }
        if (!this.solutionRepository.existsByName("pro_donate")) {
            Solution solution2 = Solution.builder()
                    .name("pro_donate")
                    .build();
            this.solutionRepository.save(solution2);
        }

        if (!this.solutionRepository.existsByName("pro_dlc")) {
            Solution solution3 = Solution.builder()
                    .name("pro_dlc")
                    .build();
            this.solutionRepository.save(solution3);
        }
    }
}
