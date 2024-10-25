package net.foodeals.payment.application.services.utils.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.paymentDetails.ChequeDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.paymentMethods.ChequePaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Currency;

@Component
@AllArgsConstructor
public class ChequeProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository repository;

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document) {
        if (document.isEmpty()) {
            throw new IllegalArgumentException("bad document");
        }

        try {
            // logic to save document on cloud.
            PartnerCommissions partnerCommission = this.repository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException(" not found "));
            ChequePaymentMethod chequePaymentMethod = new ChequePaymentMethod();
            ChequeDetails chequeDetails = (ChequeDetails) request.paymentDetails();
            chequePaymentMethod.setChequeNumber(chequeDetails.chequeNumber());
            chequePaymentMethod.setDeadlineDate(chequeDetails.deadlineDate());
            chequePaymentMethod.setRecuperationDate(chequeDetails.recuperationDate());
            chequePaymentMethod.setBank(chequeDetails.bankName());
            chequePaymentMethod.setIssuer(chequeDetails.issuer());
            chequePaymentMethod.setAmount(new Price(request.amount().amount(), Currency.getInstance(request.amount().currency())));
            partnerCommission.setPaymentMethod(chequePaymentMethod);
            partnerCommission.setPaymentDirection(PaymentDirection.FOODEALS_TO_PARTENER);
            //            chequePaymentMethod.setChequeDocument("")
            if (partnerCommission.getPaymentStatus().equals(PaymentStatus.VALIDATED_BY_PARTNER)) {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
            } else {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
            }
            this.repository.save(partnerCommission);

        } catch(Exception e) {
            throw new RuntimeException("error : payment not validated");
        }
        return new PaymentResponse("payment validated successfully");

    }

}
