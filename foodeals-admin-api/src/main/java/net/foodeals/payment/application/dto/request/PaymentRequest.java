package net.foodeals.payment.application.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.deserializers.PaymentDetailsDeserializer;
import net.foodeals.payment.application.dto.request.paymentDetails.PaymentDetails;

import java.util.UUID;

public record PaymentRequest(UUID id, String paymentMethod, PriceDto amount, @JsonDeserialize(using = PaymentDetailsDeserializer.class) PaymentDetails paymentDetails) {}