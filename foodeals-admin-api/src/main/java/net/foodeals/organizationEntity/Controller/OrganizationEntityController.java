package net.foodeals.organizationEntity.Controller;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.responses.DeliveryPartnerDto;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper.OrganizationEntityModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("v1/organizations")
public class OrganizationEntityController {
    private final OrganizationEntityService organizationEntityService;
    private final OrganizationEntityModelMapper modelMapper;

    public OrganizationEntityController(OrganizationEntityService organizationEntityService, OrganizationEntityModelMapper modelMapper) {
        this.organizationEntityService = organizationEntityService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/OrganizationEntity/add")
    public ResponseEntity<UUID> addAnOrganizationEntity(@RequestBody CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws DocumentException, IOException {
            return new ResponseEntity<>(this.organizationEntityService.createAnewOrganizationEntity(createAnOrganizationEntityDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/associations/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> createAssociation(@RequestPart("createAssociationDto") CreateAssociationDto createAssociationDto, @RequestPart("logo") MultipartFile logo, @RequestPart("cover") MultipartFile cover) {
        return new ResponseEntity<UUID>(this.organizationEntityService.createAssociation(createAssociationDto, logo, cover), HttpStatus.OK);
    }

    @PutMapping("/OrganizationEntity/{id}")
    public ResponseEntity<UUID> updateOrganizationEntity(@RequestBody UpdateOrganizationEntityDto updateOrganizationEntityDto, @PathVariable("id") UUID id) throws DocumentException, IOException {
            return new ResponseEntity<>(this.organizationEntityService.updateOrganizationEntity(id, updateOrganizationEntityDto), HttpStatus.OK);
    }

    @GetMapping("/OrganizationEntity/{id}")
    @Transactional
    public ResponseEntity<OrganizationEntityDto> getOrganizationEntityById(@PathVariable("id") UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityService.getOrganizationEntityById(id);
        OrganizationEntityDto organizationEntityDto = this.modelMapper.mapOrganizationEntity(organizationEntity);
        return new ResponseEntity<OrganizationEntityDto>(organizationEntityDto, HttpStatus.OK);
    }

    @GetMapping("/OrganizationEntities")
    @Transactional
    public ResponseEntity<Page<OrganizationEntityDto>> getOrganizationEntities(Pageable pageable) {
        Page<OrganizationEntity> organizationEntities = this.organizationEntityService.getOrganizationEntities(pageable);
        Page<OrganizationEntityDto> organizationEntitiesDto = organizationEntities.map(this.modelMapper::mapOrganizationEntity);
        return new ResponseEntity<Page<OrganizationEntityDto>>(organizationEntitiesDto, HttpStatus.OK);
    }

    @GetMapping("/delivery-partners")
    public ResponseEntity<Page<DeliveryPartnerDto>> getDeliveryPartner(Pageable pageable) {
        Page<OrganizationEntity> organizationEntities = this.organizationEntityService.getDeliveryPartners(pageable);
        Page<DeliveryPartnerDto> deliveryPartnerDtos = organizationEntities.map(this.modelMapper::mapDeliveryPartners);
        return new ResponseEntity<Page<DeliveryPartnerDto>>(deliveryPartnerDtos, HttpStatus.OK);
    }


    @PostMapping("/OrganizationEntity/{id}/validate")
    public ResponseEntity<String> validateOrganizationEntity(@PathVariable("id") UUID id) {
        return new ResponseEntity<String>(this.organizationEntityService.validateOrganizationEntity(id), HttpStatus.OK);
    }

    @GetMapping("/OrganizationEntity/{id}/Contract")
    public ResponseEntity<byte[]> getContractDocument(@PathVariable("id") UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=contract.pdf");

        return new ResponseEntity<byte []>(this.organizationEntityService.getContractDocument(id), headers, HttpStatus.OK);
    }
}
