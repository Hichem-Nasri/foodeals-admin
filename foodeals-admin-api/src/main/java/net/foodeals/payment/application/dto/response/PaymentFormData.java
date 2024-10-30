package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.Enum.PaymentMethodType;
import net.foodeals.payment.domain.entities.PartnerInfo;
import net.foodeals.user.domain.valueObjects.Name;

import java.util.UUID;

public record PaymentFormData(PaymentMethodType type, PartnerInfoDto partner, Name emitter, Price price, String documentPath, String date) {
}
