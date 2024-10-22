package net.foodeals.payment.infrastructure.controller;

import lombok.AllArgsConstructor;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/commissions/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestParam("paymentRequest") PaymentRequest paymentRequest, @RequestParam("document") MultipartFile document) throws BadRequestException {
        PaymentResponse result = paymentService.processPayment(paymentRequest,document);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions")
    public ResponseEntity<Page<CommissionPaymentDto>> getCommissionPayments(Pageable page) {
        Page<PartnerCommissions> payments = this.paymentService.getCommissionPayments(page);
        Page<CommissionPaymentDto> paymentsDtos = this.paymentService.convertCommissionToDto(payments);
        return new ResponseEntity<Page<CommissionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Page<SubscriptionPaymentDto>> getSubscriptionPayments(Pageable page) {
        Page<Subscription> subscriptions = this.paymentService.getSubscriptionPayments(page);
        Page<SubscriptionPaymentDto> paymentsDtos = subscriptions.map(this.paymentService::toSubscriptionPaymentDto);
        return new ResponseEntity<Page<SubscriptionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }
}
