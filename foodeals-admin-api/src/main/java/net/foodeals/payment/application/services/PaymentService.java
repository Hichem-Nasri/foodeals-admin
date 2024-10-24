package net.foodeals.payment.application.services;

import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.ReceiveDto;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.MonthlyOperationsDto;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PaymentService {
    Page<PartnerCommissions> getCommissionPayments(Pageable page, int year, int month);

    Page<Subscription> getSubscriptionPayments(Pageable page, int year);

    SubscriptionPaymentDto toSubscriptionPaymentDto(Subscription subscription);

    PaymentResponse processPayment(PaymentRequest paymentRequest, MultipartFile document) throws BadRequestException;

    Page<CommissionPaymentDto> convertCommissionToDto(Page<PartnerCommissions> payments);

    void paySubscription(MultipartFile document, ReceiveDto receiveDto, UUID uuid);

    Page<MonthlyOperationsDto> monthlyOperations(UUID id, int year, int month, Pageable page);
}
