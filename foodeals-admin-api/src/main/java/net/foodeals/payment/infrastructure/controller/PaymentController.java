package net.foodeals.payment.infrastructure.controller;

import lombok.AllArgsConstructor;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.request.paymentDetails.CashDetails;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.MonthlyOperationsDto;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/commissions/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processPayment(@RequestPart("dto") PaymentRequest paymentRequest, @RequestPart(value = "document", required = false) MultipartFile document) throws BadRequestException {
        PaymentResponse result = paymentService.processPayment(paymentRequest,document);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions/{year}/{month}")
    public ResponseEntity<Page<CommissionPaymentDto>> getCommissionPayments(@PathVariable("year") int year, @PathVariable int month, Pageable page) {
        Page<PartnerCommissions> payments = this.paymentService.getCommissionPayments(page, year, month);
        Page<CommissionPaymentDto> paymentsDtos = this.paymentService.convertCommissionToDto(payments);
        return new ResponseEntity<Page<CommissionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }

    @GetMapping("/commissions/{id}/monthly-operations/{year}/{month}")
    public ResponseEntity<Page<MonthlyOperationsDto>> getCommissionPayments(@PathVariable("id") UUID id, @PathVariable("year") int year, @PathVariable int month, Pageable page) {
        return new ResponseEntity<Page<MonthlyOperationsDto>>(this.paymentService.monthlyOperations(id, year, month, page), HttpStatus.OK);
    }

    @PostMapping(value = "/subscriptions/process/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getCommissionPayments(@RequestPart("document") MultipartFile document, @RequestPart("info") ReceiveDto receiveDto, @PathVariable("id") UUID id) {
        this.paymentService.paySubscription(document, receiveDto, id);
        return new ResponseEntity<String>("payment validated successfully", HttpStatus.OK);
    }

    @GetMapping("/subscriptions/{year}")
    public ResponseEntity<Page<SubscriptionPaymentDto>> getSubscriptionPayments(@PathVariable("year") int year, Pageable page) {
        Page<Subscription> subscriptions = this.paymentService.getSubscriptionPayments(page, year);
        Page<SubscriptionPaymentDto> paymentsDtos = subscriptions.map(this.paymentService::toSubscriptionPaymentDto);
        return new ResponseEntity<Page<SubscriptionPaymentDto>>(paymentsDtos, HttpStatus.OK);
    }
}
