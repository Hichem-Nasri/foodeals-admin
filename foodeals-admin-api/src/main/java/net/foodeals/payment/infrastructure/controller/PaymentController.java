package net.foodeals.payment.infrastructure.controller;

import net.foodeals.payment.application.dto.response.PaymentDto;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getPayments(Pageable page) {
        Page<Payment> payments = this.paymentService.getPayments(page);
        Page<PaymentDto> paymentsDtos = payments.map(this.paymentService::toPaymentDto);
        return new ResponseEntity<Page<PaymentDto>>(paymentsDtos, HttpStatus.OK);
    }
}
