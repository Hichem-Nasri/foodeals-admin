package net.foodeals.crm.application.dto.requests;

import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import org.springframework.lang.Nullable;

public record ProspectStatusRequest(ProspectStatus status, @Nullable UpdateReason reason) {
}
