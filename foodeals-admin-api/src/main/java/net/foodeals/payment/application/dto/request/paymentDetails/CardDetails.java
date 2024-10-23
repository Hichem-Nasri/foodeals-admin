package net.foodeals.payment.application.dto.request.paymentDetails;

public record CardDetails(String cardNumber, String cardHolderName, String expiryDate, String cvv) implements PaymentDetails {}
