package net.foodeals.organizationEntity.Controller;

import lombok.AllArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsSubEntitiesDto;
import net.foodeals.organizationEntity.application.dtos.responses.PartnerSubEntityDto;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/sub-entities")
@AllArgsConstructor
public class SubEntityController {

    private final SubEntityService subEntityService;
    private final ModelMapper mapper;

    @GetMapping("/associations/{id}")
    public ResponseEntity<Page<AssociationsSubEntitiesDto>> getFoodBankSubEntities(Pageable pageable, @PathVariable("id") UUID id) {
        Page<SubEntity> subEntities = this.subEntityService.getFoodBankSubEntities(pageable, id);
        Page<AssociationsSubEntitiesDto> associationsSubEntitiesDtos = subEntities.map(subEntity -> this.mapper.map(subEntity, AssociationsSubEntitiesDto.class));
        return new ResponseEntity<Page<AssociationsSubEntitiesDto>>(associationsSubEntitiesDtos, HttpStatus.OK);
    }

    @GetMapping("/partners/{id}")
    public ResponseEntity<Page<PartnerSubEntityDto>> partnerSubEntities(Pageable pageable, @PathVariable("id") UUID id) {
        Page<SubEntity> subEntities = this.subEntityService.partnerSubEntities(pageable, id);
        Page<PartnerSubEntityDto> partnerSubEntityDtos = subEntities.map(subEntity -> this.mapper.map(subEntity, PartnerSubEntityDto.class));
        return new ResponseEntity<Page<PartnerSubEntityDto>>(partnerSubEntityDtos, HttpStatus.OK);
    }


    @GetMapping("/{uuid}/deletion-details")
    public ResponseEntity<Page<UpdateDetails>> getDeletionDetails(@PathVariable UUID uuid, Pageable pageable) {
        Page<UpdateDetails> deletionDetails = subEntityService.getDeletionDetails(uuid, pageable);
        return ResponseEntity.ok(deletionDetails);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteSubentity(
            @PathVariable UUID uuid,
            @RequestBody UpdateReason request) {
        subEntityService.deleteSubentity(uuid, request);
        return ResponseEntity.noContent().build();
    }


}
