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
        if (this.solutionRepository.count() == 0) {
            Solution solution = Solution.builder().name("pro_market")
                    .build();
            Solution solution2 = Solution.builder().name("pro_donate")
                    .build();
            Solution solution3 = Solution.builder().name("dlc")
                    .build();
            this.solutionRepository.saveAllAndFlush(List.of(solution, solution2, solution3));
        }
    }
}
