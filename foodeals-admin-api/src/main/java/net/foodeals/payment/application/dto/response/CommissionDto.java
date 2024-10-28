package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.domain.entities.Commission;
import org.springframework.data.domain.Page;

public record CommissionDto(PaymentStatistics statistics, Page<CommissionPaymentDto> commissions) {
}
