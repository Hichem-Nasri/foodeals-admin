package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;

import java.util.UUID;

public record PaymentsDetailsDto(UUID id, boolean payable, PaymentStatus status, PaymentDirection direction) {
}
