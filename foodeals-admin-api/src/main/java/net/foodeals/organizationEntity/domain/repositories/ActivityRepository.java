package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.organizationEntity.domain.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Set<Activity> findByNameIn(List<String> activitiesNames);
    Activity findByName(String name);
}
