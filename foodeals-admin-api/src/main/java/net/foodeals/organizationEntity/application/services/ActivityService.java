package net.foodeals.organizationEntity.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.organizationEntity.application.dtos.requests.ActivityRequest;
import net.foodeals.organizationEntity.domain.entities.Activity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ActivityService extends CrudService<Activity, UUID, ActivityRequest> {

    Set<Activity> getActivitiesByName(List<String> activitiesNames);

    Activity save(Activity activity);

    Activity getActivityByName(String name);

    List<Activity> saveAll(Set<Activity> activities);
}
