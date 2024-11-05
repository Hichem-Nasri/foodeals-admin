package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;

public record MonthlyOperationsStatistics(Price total, Price commission, PaymentsDetailsDto details) {
}
