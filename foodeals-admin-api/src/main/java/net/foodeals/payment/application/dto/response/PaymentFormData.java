package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.PartnerInfo;
import net.foodeals.user.domain.valueObjects.Name;

public record PaymentFormData(PartnerInfoDto partner, Name emitter, Price price, String documentPath, String date) {
}
