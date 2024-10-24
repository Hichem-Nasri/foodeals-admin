package net.foodeals.payment.application.services.utils.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.paymentDetails.CashDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PaymentMethod;
import net.foodeals.payment.domain.entities.paymentMethods.CashPaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Currency;

@Component
@AllArgsConstructor
public class CashProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository repository;

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document) {
        try {
            PartnerCommissions partnerCommission = this.repository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id " + request.id()));
            CashPaymentMethod paymentMethod = new CashPaymentMethod();
            CashDetails cashDetails = (CashDetails) request.paymentDetails();
            paymentMethod.setAmount(new Price(request.amount().amount(), Currency.getInstance(request.amount().currency())));
            paymentMethod.setRecuperationDate(cashDetails.date());
            partnerCommission.setPaymentMethod(paymentMethod);
            partnerCommission.setPaymentDirection(PaymentDirection.FOODEALS_TO_PARTENER);
            if (partnerCommission.getPaymentStatus().equals(PaymentStatus.VALIDATED_BY_PARTNER)) {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
            } else {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
            }
            this.repository.save(partnerCommission);
        } catch (Exception e) {
            throw new RuntimeException("error : payment not validated");
        }
        return new PaymentResponse("payment validated successfully");
    }

}
