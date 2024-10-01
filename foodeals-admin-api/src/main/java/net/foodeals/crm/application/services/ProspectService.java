package net.foodeals.crm.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.crm.application.dto.requests.ProspectPartialRequest;
import net.foodeals.crm.application.dto.requests.ProspectRequest;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProspectService extends CrudService<ProspectResponse, UUID, ProspectRequest> {
    Page<ProspectResponse> findAll(Pageable pageable);

    ProspectResponse partialUpdate(UUID id, ProspectPartialRequest dto);
}
