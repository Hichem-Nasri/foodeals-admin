package net.foodeals.payment.application.dto.request.paymentDetails;

import java.util.Date;

public record CashDetails(Date date) implements PaymentDetails {
}
