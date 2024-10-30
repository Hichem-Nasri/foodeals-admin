package net.foodeals.payment.application.services.utils.implementation;

import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CardProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document, PaymentType type) {
//        CardDetails details = (CardDetails) request.paymentDetails();
//
//        // Logic for processing card payment
//
//        // Validate card, charge card, etc.
//
        return new PaymentResponse("");

    }

    @Override
    public void processCommission(PaymentRequest request) {
    }

    @Override
    public void processSubscription(PaymentRequest request) {
    }

}
