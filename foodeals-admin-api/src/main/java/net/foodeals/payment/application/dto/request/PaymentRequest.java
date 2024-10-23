package net.foodeals.payment.application.dto.request;

import net.foodeals.payment.application.dto.request.paymentDetails.PaymentDetails;

import java.util.UUID;

public record PaymentRequest(UUID id, String paymentMethod, Double amount, PaymentDetails paymentDetails) {}