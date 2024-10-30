package net.foodeals.payment.application.services.utils;

import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request, MultipartFile document, PaymentType type) throws BadRequestException;
    void processCommission(PaymentRequest request);
    void processSubscription(PaymentRequest request);

}
