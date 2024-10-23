package net.foodeals.payment.application.services.utils.implementation;

import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CashProcessor implements PaymentProcessor {
    @Override

    public PaymentResponse process(PaymentRequest request, MultipartFile document) {

        // Logic for processing cash payment

        // Record cash receipt, etc.

        return new PaymentResponse("");

    }

}
