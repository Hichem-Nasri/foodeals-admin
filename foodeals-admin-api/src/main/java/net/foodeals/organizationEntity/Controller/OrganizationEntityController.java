package net.foodeals.organizationEntity.Controller;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.requests.DeleteOrganizationRequest;
import net.foodeals.organizationEntity.application.dtos.responses.*;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper.OrganizationEntityModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.StackSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

@Controller
@RequestMapping("/api/v1/organizations")
public class OrganizationEntityController {
    private final OrganizationEntityService organizationEntityService;
    private final OrganizationEntityModelMapper modelMapper;

    public OrganizationEntityController(OrganizationEntityService organizationEntityService, OrganizationEntityModelMapper modelMapper) {
        this.organizationEntityService = organizationEntityService;
        this.modelMapper = modelMapper;
    }


        @GetMapping("/associations/form-data/{id}")
        public ResponseEntity<AssociationFormData> getAssociationFormData(@PathVariable UUID id) {
            OrganizationEntity organizationEntity = organizationEntityService.findById(id);
            return new ResponseEntity<>(this.modelMapper.mapToAssociationFormData(organizationEntity), HttpStatus.OK);
        }

    @PostMapping(value = "/partners/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAnOrganizationEntity(@RequestPart("dto") CreateAnOrganizationEntityDto createAnOrganizationEntityDto, @RequestPart(value = "logo", required = false) MultipartFile logo, @RequestPart(value = "cover", required = false) MultipartFile cover) throws DocumentException, IOException {
            OrganizationEntity  organizationEntity = this.organizationEntityService.createAnewOrganizationEntity(createAnOrganizationEntityDto, logo, cover);
            return new ResponseEntity<>(organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER) ? this.modelMapper.mapDeliveryPartners(organizationEntity) : this.modelMapper.mapOrganizationEntity(organizationEntity), HttpStatus.CREATED);
    }

    @PostMapping(value = "/associations/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<UUID> createAssociation(@RequestPart("dto") CreateAssociationDto createAssociationDto, @RequestPart(value = "logo", required = false) MultipartFile logo, @RequestPart(value = "cover", required = false) MultipartFile cover) {
        return new ResponseEntity<UUID>(this.organizationEntityService.createAssociation(createAssociationDto, logo, cover), HttpStatus.OK);
    }

    @PutMapping(value = "/associations/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<UUID> updateAssociation(@PathVariable("id") UUID id, @RequestPart("dto") CreateAssociationDto createAssociationDto, @RequestPart(value = "logo", required = false) MultipartFile logo, @RequestPart(value = "cover", required = false) MultipartFile cover) {
        return new ResponseEntity<UUID>(this.organizationEntityService.updateAssociation(id, createAssociationDto, cover, logo), HttpStatus.OK);
    }

    @PutMapping("/partners/edit/{id}")
    @Transactional
    public ResponseEntity<?> updateOrganizationEntity(@RequestBody CreateAnOrganizationEntityDto updateOrganizationEntityDto, @PathVariable("id") UUID id) throws DocumentException, IOException {
        OrganizationEntity  organizationEntity = this.organizationEntityService.updateOrganizationEntity(id, updateOrganizationEntityDto);
        return new ResponseEntity<>(organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER) ? this.modelMapper.mapDeliveryPartners(organizationEntity) : this.modelMapper.mapOrganizationEntity(organizationEntity), HttpStatus.OK);
    }

    @GetMapping("/partners/form-data/{id}")
    @Transactional
    public ResponseEntity<?> getFormData(@PathVariable("id") UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityService.getOrganizationEntityById(id);
        return new ResponseEntity<>(organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER) ?   this.modelMapper.convertToDeliveryFormData(organizationEntity) : this.modelMapper.convertToFormData(organizationEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable UUID uuid,
            @RequestBody DeleteOrganizationRequest request) {
        organizationEntityService.deleteOrganization(uuid, request.getReason(), request.getDetails());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partners/deleted")
    @Transactional
    public ResponseEntity<Page<?>> getDeletedOrganizationsPaginated(Pageable pageable, @RequestParam(value = "type", required = true) List<EntityType> type) {
        Page<OrganizationEntity> deletedOrganizations = organizationEntityService.getDeletedOrganizationsPaginated(pageable, type);

        // Create sets of related types
        Set<EntityType> associationTypes = Set.of(EntityType.ASSOCIATION, EntityType.FOOD_BANK_ASSO, EntityType.FOOD_BANK);
        Set<EntityType> deliveryTypes = Set.of(EntityType.DELIVERY_PARTNER);
        Set<EntityType> partnerTypes = Set.of(EntityType.NORMAL_PARTNER, EntityType.PARTNER_WITH_SB);

        // Check if type list has any common elements with our defined sets
        if (!Collections.disjoint(type, associationTypes)) {
            return new ResponseEntity<>(deletedOrganizations.map(d -> this.modelMapper.mapToAssociation(d)), HttpStatus.OK);
        }
        else if (!Collections.disjoint(type, deliveryTypes)) {
            return new ResponseEntity<>(deletedOrganizations.map(d -> this.modelMapper.mapDeliveryPartners(d)), HttpStatus.OK);
        }
        else if (!Collections.disjoint(type, partnerTypes)) {
            return new ResponseEntity<>(deletedOrganizations.map(d -> this.modelMapper.mapOrganizationEntity(d)), HttpStatus.OK);
        }
        else {
            return null;
        }
    }

    @GetMapping("/partners/{id}")
    @Transactional
    public ResponseEntity<OrganizationEntityDto> getOrganizationEntityById(@PathVariable("id") UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityService.getOrganizationEntityById(id);
        OrganizationEntityDto organizationEntityDto = this.modelMapper.mapOrganizationEntity(organizationEntity);
        return new ResponseEntity<OrganizationEntityDto>(organizationEntityDto, HttpStatus.OK);
    }

    @GetMapping("/{uuid}/deletion-details")
    public ResponseEntity<DeletionDetailsDTO> getDeletionDetails(@PathVariable UUID uuid) {
        DeletionDetailsDTO deletionDetails = organizationEntityService.getDeletionDetails(uuid);
        return ResponseEntity.ok(deletionDetails);
    }

    @GetMapping("/partners")
    @Transactional
    public ResponseEntity<Page<OrganizationEntityDto>> getOrganizationEntities(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(value = "types", required = false)
            List<EntityType> types,

            @RequestParam(value = "names", required = false)
            List<String> names,

            @RequestParam(value = "solutions", required = false)
            List<String> solutions,

            @RequestParam(value = "cityId", required = false)
            UUID cityId,

            @RequestParam(value = "collabId", required = false)
            Long collabId,

            @RequestParam(value = "email", required = false)
            String email,

            @RequestParam(value = "phone", required = false)
            String phone,

            Pageable pageable
    ) {
        types = types != null ? types : List.of(EntityType.NORMAL_PARTNER, EntityType.PARTNER_WITH_SB);
        OrganizationEntityFilter filter = OrganizationEntityFilter.builder()
                .startDate(startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null)
                .endDate(endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null)
                .types(types)
                .names(names)
                .email(email)
                .phone(phone)
                .solutions(solutions)
                .cityId(cityId)
                .collabId(collabId)
                .build();

        Page<OrganizationEntity> organizationEntities = organizationEntityService.getOrganizationEntitiesFilters(filter, pageable);
        Page<OrganizationEntityDto> organizationEntitiesDto = organizationEntities.map(modelMapper::mapOrganizationEntity);
        return ResponseEntity.ok(organizationEntitiesDto);
    }

    // elevery partner update, update contract, getcontract

    @GetMapping("/delivery-partners")
    public ResponseEntity<Page<DeliveryPartnerDto>> getDeliveryPartner(Pageable pageable) {
        Page<OrganizationEntity> organizationEntities = this.organizationEntityService.getDeliveryPartners(pageable);
        Page<DeliveryPartnerDto> deliveryPartnerDtos = organizationEntities.map(this.modelMapper::mapDeliveryPartners);
        return new ResponseEntity<Page<DeliveryPartnerDto>>(deliveryPartnerDtos, HttpStatus.OK);
    }


    @PostMapping(value = "/partners/validate/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateOrganizationEntity(@PathVariable("id") UUID id, @RequestPart("document") MultipartFile file) {
        return new ResponseEntity<String>(this.organizationEntityService.validateOrganizationEntity(id, file), HttpStatus.OK);
    }

    @GetMapping("/partners/contracts/{id}")
    public ResponseEntity<byte[]> getContractDocument(@PathVariable("id") UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=contract.pdf");

        return new ResponseEntity<byte []>(this.organizationEntityService.getContractDocument(id), headers, HttpStatus.OK);
    }

    @GetMapping("/associations")
    @Transactional
    public ResponseEntity<Page<AssociationsDto>> getAssociations(Pageable pageable) {
        Page<OrganizationEntity> organizationEntities = this.organizationEntityService.getAssociations(pageable);
        Page<AssociationsDto> associationsDtos = organizationEntities.map(this.modelMapper::mapToAssociation);
        return new ResponseEntity<Page<AssociationsDto>>(associationsDtos, HttpStatus.OK);
    }
}

// 1 week
