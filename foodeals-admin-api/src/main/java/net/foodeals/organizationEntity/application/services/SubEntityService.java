package net.foodeals.organizationEntity.application.services;

import lombok.AllArgsConstructor;
import net.foodeals.common.contracts.CrudService;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.SubEntityResponse;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SubEntityService extends CrudService<SubEntityResponse, UUID, SubEntityRequest> {
    Integer countByOrganizationEntity_IdAndType(UUID organizationId, SubEntityType type);

    Page<SubEntity> getFoodBankSubEntities(Pageable pageable, UUID id);
}
