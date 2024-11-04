package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DeadlinesDto {

    private UUID id;

    private String date;

    private DeadlineStatus deadlineStatus;

    private Price amount;

    private Boolean payable;
}
