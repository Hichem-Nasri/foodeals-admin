package net.foodeals.organizationEntity.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.organizationEntity.application.dtos.requests.ActivityRequest;
import net.foodeals.organizationEntity.application.services.ActivityService;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.exceptions.ActivityNotFoundException;
import net.foodeals.organizationEntity.domain.repositories.ActivityRepository;
import net.foodeals.processors.classes.DtoProcessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository repository;
    private final DtoProcessor dtoProcessor;

    @Override
    public List<Activity> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Page<Activity> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<Activity> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    public Activity findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    @Override
    public Activity create(ActivityRequest dto) {
        Activity activity = Activity.builder().name(dto.name().toLowerCase()).build();
        return this.repository.save(activity);
    }

    @Override
    public Activity update(UUID id, ActivityRequest dto) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));

        activity.setName(dto.name().toLowerCase());
        return this.repository.save(activity);
    }

    @Override
    public void delete(UUID id) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));

        this.repository.softDelete(activity.getId());
    }

    @Override
    public Set<Activity> getActivitiesByName(List<String>  activitiesNames) {
        return this.repository.findByNameIn(activitiesNames.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    @Override
    public Activity getActivityByName(String name) {
        return this.repository.findByName(name.toLowerCase());
    }

    @Override
    public List<Activity> saveAll(Set<Activity> activities) {
        return this.repository.saveAll(activities);
    }

    @Override
    public Activity save(Activity activity) {
        return this.repository.save(activity);
    }
}
