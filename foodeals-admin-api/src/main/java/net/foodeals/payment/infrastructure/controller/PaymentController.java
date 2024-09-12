package net.foodeals.payment.infrastructure.controller;

import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.domain.entities.Payment;
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

    @GetMapping("/commissions")
    public ResponseEntity<Page<CommissionPaymentDto>> getCommissionPayments(Pageable page) {
        Page<Payment> payments = this.paymentService.getCommissionPayments(page);
        Page<CommissionPaymentDto> paymentsDtos = payments.map(this.paymentService::toCommissionPaymentDto);
        return new ResponseEntity<Page<CommissionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Page<SubscriptionPaymentDto>> getSubscriptionPayments(Pageable page) {
        Page<Subscription> subscriptions = this.paymentService.getSubscriptionPayments(page);
        Page<SubscriptionPaymentDto> paymentsDtos = subscriptions.map(this.paymentService::toSubscriptionPaymentDto);
        return new ResponseEntity<Page<SubscriptionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }
}
