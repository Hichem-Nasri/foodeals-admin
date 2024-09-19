package net.foodeals.payment.application.dto.response;

import lombok.Data;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;

@Data
public class CommissionPaymentDto {
    private String id;

    private String date;

    private PartnerInfoDto partnerInfoDto;
    
    private PartnerType partnerType;

    private String numberOfOrders;

    private String foodealsCommission;

    private String toPay;

    private String toReceive;

    private PaymentStatus paymentStatus;
}
