package net.foodeals.crm.application.dto.requests;

import net.foodeals.crm.domain.entities.enums.ProspectStatus;

public record ProspectStatusRequest(ProspectStatus status) {
}
