package net.foodeals.organizationEntity.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.organizationEntity.application.dtos.requests.ActivityRequest;
import net.foodeals.organizationEntity.domain.entities.Activity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ActivityService extends CrudService<Activity, UUID, ActivityRequest> {

    Set<Activity> getActivitiesByName(List<String> subActivitiesNames);

    Activity save(Activity mainActivity);

    Activity getActivityByName(String name);
}
