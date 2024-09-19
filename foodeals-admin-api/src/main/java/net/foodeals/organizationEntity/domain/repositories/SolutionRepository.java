package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.organizationEntity.domain.entities.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<Solution, UUID> {
    Set<Solution> findByNameIn(List<String> solutionsNames);
    Solution    findByName(String name);
}
