package net.foodeals.organizationEntity.application.services;

import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.repositories.SolutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SolutionService {

    private final SolutionRepository solutionRepository;


    public SolutionService(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    public Set<Solution> getSolutionsByNames(List<String> solutionsNames) {
        return this.solutionRepository.findByNameIn(solutionsNames);
    }

    public Solution save(Solution solution) {
        return this.solutionRepository.save(solution);
    }

    public Solution findByName(String solution) {
        return this.solutionRepository.findByName(solution);
    }
}
