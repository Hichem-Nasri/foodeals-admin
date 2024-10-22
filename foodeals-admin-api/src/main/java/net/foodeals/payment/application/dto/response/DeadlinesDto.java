package net.foodeals.payment.application.dto.response;

import lombok.Data;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DeadlinesDto {

    private UUID id;
    private String dueDate;

    private DeadlineStatus deadlineStatus;

    private BigDecimal deadlineAmount;
}
