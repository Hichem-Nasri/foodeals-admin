//package net.foodeals.payment.application.dto.response;
//
//import lombok.Data;
//import net.foodeals.payment.domain.entities.Enum.PartnerType;
//import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
//
//@Data
////public class CommissionPaymentDto {
////    private String id;
////
////    private String date;
////
////    private PartnerInfoDto partnerInfoDto;
////
////    private PartnerType partnerType;
////
////    private String numberOfOrders;
////
////    private String foodealsCommission;
////
////    private String toPay;
////
////    private String toReceive;
////
////    private PaymentStatus paymentStatus;
////}
package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CommissionPaymentDto {
    private UUID id;

    private UUID entityId;

    private UUID organizationId;

    private String date;

    private PartnerInfoDto partnerInfoDto;

    private PartnerType partnerType;

    private Double totalAmount;

    private Double foodealsCommission;

    private Double toPay;

    private Double toReceive;

    private PaymentStatus paymentStatus;

    private boolean payable;

    private  boolean commissionPayedBySubEntities;
}

