package net.foodeals.payment.application.dto.response;

import lombok.Data;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;

import java.math.BigDecimal;

@Data
public class DeadlinesDto {

    private String dueDate;

    private DeadlineStatus deadlineStatus;

    private BigDecimal deadlineAmount;
}
