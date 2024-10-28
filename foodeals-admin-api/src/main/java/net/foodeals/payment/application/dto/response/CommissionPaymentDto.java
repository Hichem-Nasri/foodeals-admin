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
import lombok.NoArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommissionPaymentDto {
    private UUID id;

    private UUID entityId;

    private UUID organizationId;

    private String date;

    private PartnerInfoDto partnerInfoDto;

    private PartnerType partnerType;

    private Price totalAmount;

    private Price foodealsCommission;

    private Price toPay;

    private Price toReceive;

    private PaymentStatus paymentStatus;

    private boolean payable;

    private  boolean commissionPayedBySubEntities;
}

