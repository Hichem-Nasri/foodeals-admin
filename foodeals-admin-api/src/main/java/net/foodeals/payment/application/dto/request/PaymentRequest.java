package net.foodeals.payment.application.dto.request;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.paymentDetails.PaymentDetails;

import java.util.UUID;

public record PaymentRequest(UUID id, String paymentMethod, PriceDto amount, PaymentDetails paymentDetails) {}