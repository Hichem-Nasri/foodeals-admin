package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public record MonthlyOperationsDto(PartnerInfoDto partner, PaymentStatistics statistics, Page<OperationsDto> operations, PaymentsDetailsDto details) {
}
