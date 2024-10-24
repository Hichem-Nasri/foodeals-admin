package net.foodeals.payment.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MonthlyOperationsDto(ProductInfo product, UUID id, BigDecimal amount, Integer quantity, BigDecimal cashAmount, BigDecimal cashCommission, BigDecimal cardAmount, BigDecimal commissionCard) {
}
