package net.foodeals.organizationEntity.application.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.common.entities.DeletionReason;
import net.foodeals.location.domain.entities.City;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityFilter;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.SubEntityResponse;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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


    @Override
    @Transactional
    public void deleteSubentity(UUID uuid, UpdateReason reason) {
        SubEntity subEntity = subEntityRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("SubEntity not found with uuid: " + uuid));
        DeletionReason deletionReason = DeletionReason.builder()
                .details(reason.details())
                .reason(reason.reason())
                .type(reason.action())
                .build();
        subEntity.getDeletionReasons().add(deletionReason);
        subEntity.markDeleted(reason.action());
        subEntityRepository.save(subEntity);
    }

    @Transactional
    public Page<UpdateDetails> getDeletionDetails(UUID uuid, Pageable page) {
        SubEntity subEntity = this.subEntityRepository.getEntity(uuid)
                .orElseThrow(() -> new EntityNotFoundException("SubEntity not found with uuid: " + uuid));

        List<DeletionReason> deletionReasons = subEntity.getDeletionReasons();

        int start = (int)page.getOffset();
        int end = Math.min(start + page.getPageSize(), deletionReasons.size());
        List<DeletionReason> content = deletionReasons.subList(start, end);

        return new PageImpl<>(content, page, deletionReasons.size()).map(d -> new UpdateDetails(d.getType(), d.getDetails(), d.getReason(), Date.from(d.getCreatedAt())));
    }

    @Override
    public Page<SubEntity> subEntitiesFilters(Pageable pageable, UUID id, SubEntityFilter filter) {
        return this.subEntityRepository.findWithFilters(id, filter, pageable);    }

    @Override
    public Page<City> searchCitiesBySubEntityAddress(String cityName, UUID organizationId, Pageable pageable) {
        return this.subEntityRepository.findCitiesByOrganizationIdAndCityName(organizationId, cityName,pageable);
    }

}
