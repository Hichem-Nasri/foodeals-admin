package net.foodeals.organizationEntity.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.SubEntityResponse;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SubEntityService extends CrudService<SubEntityResponse, UUID, SubEntityRequest> {
    Integer countByOrganizationEntity_IdAndType(UUID organizationId, SubEntityType type);

    Page<SubEntity> getFoodBankSubEntities(Pageable pageable, UUID id);

    Page<SubEntity> partnerSubEntities(Pageable pageable, UUID id);
    SubEntity getEntityById(UUID id);

    void deleteSubentity(UUID uuid, UpdateReason reason);

    Page<UpdateDetails> getDeletionDetails(UUID uuid, Pageable page);
}
