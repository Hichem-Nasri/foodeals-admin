package net.foodeals.payment.application.dto.request;

import java.math.BigDecimal;

public record PriceDto(BigDecimal amount, String currency) {
}
