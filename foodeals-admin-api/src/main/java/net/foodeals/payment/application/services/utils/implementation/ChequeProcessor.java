package net.foodeals.payment.application.services.utils.implementation;

import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.paymentDetails.ChequeDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ChequeProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document) {

        ChequeDetails details = (ChequeDetails) request.paymentDetails();

        // Logic for processing cheque payment

        // Record cheque details, mark as pending clearance, etc.

        return new PaymentResponse("");

    }

}
