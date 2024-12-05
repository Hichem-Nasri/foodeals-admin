package net.foodeals.organizationEntity.Controller;

import net.foodeals.organizationEntity.application.dtos.requests.ActivityRequest;
import net.foodeals.organizationEntity.application.dtos.requests.FeatureRequest;
import net.foodeals.organizationEntity.application.dtos.responses.ActivityResponseDto;
import net.foodeals.organizationEntity.application.dtos.responses.FeatureResponse;
import net.foodeals.organizationEntity.application.services.ActivityService;
import net.foodeals.organizationEntity.application.services.FeatureService;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.Features;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {
    private final FeatureService featureService;
    private final ModelMapper modelMapper;

    public FeatureController(FeatureService featureService, ModelMapper modelMapper) {
        this.featureService = featureService;
        this.modelMapper = modelMapper;
    }

    // Get All Features
    @GetMapping
    @Transactional
    public ResponseEntity<Page<FeatureResponse>> getAllFeatures(Pageable pageable) {
        Page<Features> features = featureService.findAll(pageable);
        Page<FeatureResponse> featureResponses = features
                .map(feature -> modelMapper.map(feature, FeatureResponse.class));
        return new ResponseEntity<>(featureResponses, HttpStatus.OK);
    }

    // Get Feature by ID
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<FeatureResponse> getFeatureById(@PathVariable("id") UUID id) {
        Features feature = featureService.findById(id);
        FeatureResponse featureResponse = modelMapper.map(feature, FeatureResponse.class);
        return new ResponseEntity<>(featureResponse, HttpStatus.OK);
    }

    // Create a Feature
    @PostMapping
    @Transactional
    public ResponseEntity<FeatureResponse> createFeature(@RequestBody FeatureRequest featureRequest) {
        Features feature = featureService.create(featureRequest);
        FeatureResponse featureResponse = modelMapper.map(feature, FeatureResponse.class);
        return new ResponseEntity<>(featureResponse, HttpStatus.CREATED);
    }

    // Update a Feature
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<FeatureResponse> updateFeature(@RequestBody FeatureRequest featureRequest, @PathVariable("id") UUID id) {
        Features feature = featureService.update(id, featureRequest);
        FeatureResponse featureResponse = modelMapper.map(feature, FeatureResponse.class);
        return new ResponseEntity<>(featureResponse, HttpStatus.OK);
    }

    // Delete a Feature
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteFeature(@PathVariable("id") UUID id) {
        featureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}