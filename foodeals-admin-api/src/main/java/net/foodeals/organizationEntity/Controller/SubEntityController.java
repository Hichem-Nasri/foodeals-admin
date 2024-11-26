package net.foodeals.organizationEntity.Controller;

import lombok.AllArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityFilter;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsSubEntitiesDto;
import net.foodeals.organizationEntity.application.dtos.responses.PartnerSubEntityDto;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.OrganizationsType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/sub-entities")
@AllArgsConstructor
public class SubEntityController {

    private final SubEntityService subEntityService;
    private final ModelMapper mapper;

    @GetMapping("/organizations/{id}")
    public ResponseEntity<Page<?>> partnerSubEntities(
            Pageable pageable,
            @PathVariable("id") UUID id,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(value = "organizationsType", required = true)
            OrganizationsType organizationsType,

            @RequestParam(value = "names", required = false)
            List<String> names,

            @RequestParam(value = "types", required = true)
            List<SubEntityType> types,

            @RequestParam(value = "solutions", required = false)
            List<String> solutions,

            @RequestParam(value = "cityId", required = false)
            UUID cityId,

            @RequestParam(value = "collabId", required = false)
            Integer collabId,

            @RequestParam(value = "email", required = false)
            String email,

            @RequestParam(value = "phone", required = false)
            String phone,

            @RequestParam(value = "deletedAt", required = true)
            Boolean deletedAt
    ) {
        System.out.println("test");
        // Create a filter object with the provided parameters
        SubEntityFilter filter = SubEntityFilter.builder()
                .startDate(startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null)
                .endDate(endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null)
                .names(names)
                .email(email)
                .phone(phone)
                .solutions(solutions != null ? solutions : new ArrayList<String>())
                .cityId(cityId)
                .types(types)
                .collabId(collabId)
                .deletedAt(deletedAt)
                .build();

        // Fetch the sub-entities based on the filter
        Page<SubEntity> subEntities = this.subEntityService.subEntitiesFilters(pageable, id, filter);
        return switch (organizationsType) {
            case ASSOCIATIONS -> new ResponseEntity<>(subEntities.map(subEntity -> this.mapper.map(subEntity, AssociationsSubEntitiesDto.class)), HttpStatus.OK);
            case PARTNERS -> new ResponseEntity<>(subEntities.map(subEntity -> this.mapper.map(subEntity, PartnerSubEntityDto.class)), HttpStatus.OK);
            default -> null;
        };
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
