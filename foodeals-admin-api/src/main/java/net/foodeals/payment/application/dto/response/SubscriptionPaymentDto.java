package net.foodeals.payment.application.dto.response;

import lombok.Data;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubscriptionPaymentDto {

    private String id;

    private PartnerInfoDto partnerInfoDto;

    private PartnerType partnerType;

    private BigDecimal totalAmount;

    private List<DeadlinesDto> deadlinesDtoList;

    private SubscriptionStatus subscriptionStatus;

    private List<String> solutions;
}
