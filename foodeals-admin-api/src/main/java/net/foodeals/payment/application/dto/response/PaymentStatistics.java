package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;

public record PaymentStatistics(Price total, Price totalCommission) {
}