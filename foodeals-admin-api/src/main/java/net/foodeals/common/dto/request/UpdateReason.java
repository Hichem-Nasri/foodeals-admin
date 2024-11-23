package net.foodeals.common.dto.request;

import net.foodeals.common.entities.enums.ActionType;
import net.foodeals.common.entities.enums.ReasonType;

public record UpdateReason(ActionType action, ReasonType reason, String details) {
}
