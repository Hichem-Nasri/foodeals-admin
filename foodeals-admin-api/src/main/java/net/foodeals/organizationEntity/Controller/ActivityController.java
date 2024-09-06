package net.foodeals.organizationEntity.Controller;

import net.foodeals.organizationEntity.application.dtos.requests.ActivityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.ActivityResponseDto;
import net.foodeals.organizationEntity.application.services.ActivityService;
import net.foodeals.organizationEntity.domain.entities.Activity;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ModelMapper modelMapper;


    public ActivityController(ActivityService activityService, ModelMapper modelMapper) {
        this.activityService = activityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/Activities")
    public ResponseEntity<List<ActivityResponseDto>> getAllActivities() {
        List<Activity> activities = this.activityService.findAll();
        List<ActivityResponseDto> activityResponses = activities.stream().map(activity -> this.modelMapper.map(activity, ActivityResponseDto.class)).toList();
        return new ResponseEntity<List<ActivityResponseDto>>(activityResponses, HttpStatus.OK);
    }

    @GetMapping("/Activity/{id}")
    public ResponseEntity<ActivityResponseDto> getActivityById(@PathVariable("id") UUID id) {
        Activity activity = this.activityService.findById(id);
        ActivityResponseDto activityResponse = this.modelMapper.map(activity, ActivityResponseDto.class);
        return new ResponseEntity<ActivityResponseDto>(activityResponse, HttpStatus.OK);
    }

    @PostMapping("/Activities")
    public ResponseEntity<ActivityResponseDto> createAnActivity(@RequestBody ActivityRequest activityRequest) {
        Activity activity = this.activityService.create(activityRequest);
        ActivityResponseDto activityResponse = this.modelMapper.map(activity, ActivityResponseDto.class);
        return new ResponseEntity<ActivityResponseDto>(activityResponse, HttpStatus.CREATED);
    }

    @PutMapping("/Activity/{id}")
    public ResponseEntity<ActivityResponseDto> updateAnActivity(@RequestBody ActivityRequest activityRequest, @PathVariable("id") UUID id) {
        Activity activity = this.activityService.update(id, activityRequest);
        ActivityResponseDto activityResponse = this.modelMapper.map(activity, ActivityResponseDto.class);
        return new ResponseEntity<ActivityResponseDto>(activityResponse, HttpStatus.OK);
    }

    @DeleteMapping("/Activity/{id}")
    public ResponseEntity<String> deleteAnActivity(@PathVariable("id") UUID id) {
        this.activityService.delete(id);
        return new ResponseEntity<String>("Activity has been deleted", HttpStatus.OK);
    }
}

