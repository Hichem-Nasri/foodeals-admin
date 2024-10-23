package net.foodeals.payment.application.dto.request;

import java.math.BigDecimal;
import java.util.Date;

public record ReceiveDto(Date date, String emitter) {
}
