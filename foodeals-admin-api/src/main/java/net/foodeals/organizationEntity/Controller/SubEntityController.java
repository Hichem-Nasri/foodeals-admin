package net.foodeals.organizationEntity.Controller;

import lombok.AllArgsConstructor;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsSubEntitiesDto;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsDto;
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
}
