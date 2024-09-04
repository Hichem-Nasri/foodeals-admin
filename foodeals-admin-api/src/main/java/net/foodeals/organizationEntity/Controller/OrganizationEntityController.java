package net.foodeals.organizationEntity.Controller;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Header;
import net.foodeals.organizationEntity.application.dto.response.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dto.upload.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dto.upload.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
public class OrganizationEntityController {
    private final OrganizationEntityService organizationEntityService;

    public OrganizationEntityController(OrganizationEntityService organizationEntityService) {
        this.organizationEntityService = organizationEntityService;
    }

    @PostMapping("/OrganizationEntity/add")
    public ResponseEntity<UUID> addAnOrganizationEntity(@RequestBody CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws DocumentException, IOException {
            return new ResponseEntity<>(this.organizationEntityService.createAnewOrganizationEntity(createAnOrganizationEntityDto), HttpStatus.CREATED);
    }

    @PutMapping("/OrganizationEntity/{id}")
    public ResponseEntity<String> updateOrganizationEntity(@RequestBody UpdateOrganizationEntityDto updateOrganizationEntityDto, @PathVariable("id") UUID id) throws DocumentException, IOException {
            return new ResponseEntity<>(this.organizationEntityService.updateOrganizationEntity(id, updateOrganizationEntityDto), HttpStatus.OK);
    }
}
