package net.foodeals.payment.infrastructure.controller;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.response.*;
import net.foodeals.payment.application.services.PaymentService;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
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

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processPayment(@RequestPart("dto") PaymentRequest paymentRequest, @RequestPart(value = "document", required = false) MultipartFile document, @RequestParam(value = "type", required = true) PaymentType type) throws BadRequestException {
        PaymentResponse result = paymentService.processPayment(paymentRequest,document, type);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions/{year}/{month}")
    public ResponseEntity<CommissionDto> getCommissionPayments(@PathVariable("year") int year, @PathVariable int month, Pageable page, @RequestParam(value = "id", required = false) UUID id) {
        List<PartnerCommissions> payments = id == null ? this.paymentService.getCommissionPayments(year, month) : this.paymentService.getCommissionPaymentsByOrganizationId(id, year, month);
        return new ResponseEntity<CommissionDto>(this.paymentService.getCommissionResponse(payments, page), HttpStatus.OK);

    }

    @Transactional
    @GetMapping("/commissions/delivery/{id}/{year}")
    public ResponseEntity<DeliveryPaymentResponse> getDeliveryPayments(@PathVariable("year") int year, Pageable page, @PathVariable(value = "id") UUID id) {
        return new ResponseEntity<DeliveryPaymentResponse>(this.paymentService.getDeliveryPayments(year, page, id), HttpStatus.OK);

    }

    @GetMapping("/form-data/{id}")
    public ResponseEntity<PaymentFormData> getFormData(@PathVariable("id") UUID id, @RequestParam(value = "type", required = true) PaymentType type) {
        return new ResponseEntity<PaymentFormData>(this.paymentService.getFormData(id, type), HttpStatus.OK);

    }

    @GetMapping("/commissions/{id}/monthly-operations/{year}/{month}")
    public ResponseEntity<MonthlyOperationsDto> getCommissionPayments(@PathVariable("id") UUID id, @PathVariable("year") int year, @PathVariable("month") int month, @RequestParam(value = "type", required = true) PartnerType type, Pageable page) {
        return new ResponseEntity<MonthlyOperationsDto>(this.paymentService.monthlyOperations(id, year, month, page, type), HttpStatus.OK);
    }

    @PostMapping(value = "/receive")
    public ResponseEntity<PaymentResponse> receive(@RequestBody ReceiveDto receiveDto, @RequestParam(value = "type", required = true) PaymentType type) throws BadRequestException {
        return new ResponseEntity<PaymentResponse>(this.paymentService.receive(receiveDto, type), HttpStatus.OK);
    }

    @GetMapping("/subscriptions/{year}")
    public ResponseEntity<SubscriptionPaymentDto> getSubscriptionPayments(@PathVariable("year") int year, Pageable pageable, @RequestParam(value = "id", required = false) UUID id) {
        return new ResponseEntity<SubscriptionPaymentDto>(this.paymentService.getSubscriptionResponse(year, pageable, id), HttpStatus.OK);
    }

    @GetMapping("/subscriptions/{year}/{id}")
    public ResponseEntity<SubscriptionDetails> getSubscriptionsDetails(@PathVariable("year") int year, @PathVariable(value = "id") UUID id) {
        return new ResponseEntity<SubscriptionDetails>(this.paymentService.getSubscriptionDetails(year, id), HttpStatus.OK);
    }
}


// should check pages of commissions.
// and all paginations from list.
// create partner with sub payed by sub
// pay commission by sub
// recive it
// chack status and all data.

