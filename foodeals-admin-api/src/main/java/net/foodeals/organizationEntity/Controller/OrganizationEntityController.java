package net.foodeals.organizationEntity.Controller;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper.OrganizationEntityModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
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

    @PutMapping("/OrganizationEntity/{id}")
    public ResponseEntity<String> updateOrganizationEntity(@RequestBody UpdateOrganizationEntityDto updateOrganizationEntityDto, @PathVariable("id") UUID id) throws DocumentException, IOException {
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
    public ResponseEntity<List<OrganizationEntityDto>> getOrganizationEntities() {
        List<OrganizationEntity> organizationEntities = this.organizationEntityService.getOrganizationEntities();
        List<OrganizationEntityDto> organizationEntitiesDto = organizationEntities.stream().map(this.modelMapper::mapOrganizationEntity).toList();
        return new ResponseEntity<List<OrganizationEntityDto>>(organizationEntitiesDto, HttpStatus.OK);
    }
}
