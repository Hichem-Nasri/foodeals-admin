package net.foodeals.payment.application.dto.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ReceiveDto(UUID id, Date date, String emitter) {
}
