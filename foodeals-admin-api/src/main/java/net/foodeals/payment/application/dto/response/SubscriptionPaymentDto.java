package net.foodeals.payment.application.dto.response;

import lombok.Data;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public record SubscriptionPaymentDto(SubscriptionStatistics statistics, Page<SubscriptionsListDto> list) {}
