package net.foodeals.organizationEntity.application.services.impl;

import lombok.AllArgsConstructor;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.SubEntityResponse;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SubEntityServiceImpl implements SubEntityService {

    private final SubEntityRepository subEntityRepository;

    @Override
    public Integer countByOrganizationEntity_IdAndType(UUID organizationId, SubEntityType type) {
        return this.subEntityRepository.countByOrganizationEntity_IdAndType(organizationId, type);
    }

    @Override
    public Page<SubEntity> getFoodBankSubEntities(Pageable pageable, UUID id) {
        return this.subEntityRepository.findByOrganizationEntity_Id(id, pageable);
    }

    @Override
    public Page<SubEntity> partnerSubEntities(Pageable pageable, UUID id) {
        return this.subEntityRepository.findByOrganizationEntity_Id(id, pageable);
    }

    @Override
    public List<SubEntityResponse> findAll() {
        return List.of();
    }

    @Override
    public Page<SubEntityResponse> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<SubEntityResponse> findAll(Pageable pageable) {
        return null;
    }


    @Override
    public SubEntityResponse findById(UUID uuid) {
        return null;
    }

    @Override
    public SubEntityResponse create(SubEntityRequest dto) {
        return null;
    }

    @Override
    public SubEntityResponse update(UUID uuid, SubEntityRequest dto) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {
    }

    @Override
    public SubEntity getEntityById(UUID id) {
        return this.subEntityRepository.findById(id).orElse(null);
    }


}
