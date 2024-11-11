package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.organizationEntity.domain.entities.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ActivityRepository extends BaseRepository<Activity, UUID> {

    Set<Activity> findByNameIn(List<String> activitiesNames);
    Activity findByName(String name);

    boolean existsByName(String s);
    Page<Activity> findByTypeIn(List<String> types, Pageable pageable);
}
