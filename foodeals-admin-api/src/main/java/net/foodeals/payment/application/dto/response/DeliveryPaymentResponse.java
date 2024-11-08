package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class DeliveryPaymentResponse {
    private PaymentStatistics statistics;
    private Page<DeliveryPaymentDto> payments;
}
