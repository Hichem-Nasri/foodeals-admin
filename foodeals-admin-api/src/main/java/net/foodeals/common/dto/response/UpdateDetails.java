package net.foodeals.common.dto.response;


import net.foodeals.common.entities.enums.ActionType;
import net.foodeals.common.entities.enums.ReasonType;

import java.util.Date;

public record UpdateDetails(ActionType action, String details, ReasonType reason, Date deletedAt) {
}
