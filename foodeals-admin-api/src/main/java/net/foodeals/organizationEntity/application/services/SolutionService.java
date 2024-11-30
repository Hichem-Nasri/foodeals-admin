package net.foodeals.organizationEntity.application.services;

import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.repositories.SolutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SolutionService {

    private final SolutionRepository solutionRepository;


    public SolutionService(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    public Set<Solution> getSolutionsByNames(List<String> solutionsNames) {
        return this.solutionRepository.findByNameIn(solutionsNames.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    @Transactional
    public Solution save(Solution solution) {
        return this.solutionRepository.save(solution);
    }

    public Solution findByName(String solution) {
        return this.solutionRepository.findByName(solution.toLowerCase());
    }

    public List<Solution> saveAll(Set<Solution> solutions) {
        return this.solutionRepository.saveAll(solutions);
    }
}
