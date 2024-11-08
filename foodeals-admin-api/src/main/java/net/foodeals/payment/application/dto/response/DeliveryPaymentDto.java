package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPaymentDto {
    private String month;
    private Price deliveryCost;
    private Price commissionCost;
    private Long orderCount;
    private Price foodealsCommission;
    private Price amountToPay;
    private Price amountToReceive;
    private PaymentStatus status;
}