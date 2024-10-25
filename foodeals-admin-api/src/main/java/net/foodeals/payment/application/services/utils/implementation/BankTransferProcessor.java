package net.foodeals.payment.application.services.utils.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.paymentDetails.BankTransferDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PaymentMethod;
import net.foodeals.payment.domain.entities.paymentMethods.BankTransferPaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Currency;

@Component
@AllArgsConstructor
public class BankTransferProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository partnerCommissionsRepository;

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document) throws BadRequestException {

        if (document.isEmpty()) {
            throw new BadRequestException("bad document");
        }
        try {
            //        // logic to upload file into cloud storage.
            PartnerCommissions partnerCommission = this.partnerCommissionsRepository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id : " + request.id().toString()));
            if (partnerCommission.getPaymentStatus().equals(PaymentStatus.VALIDATED_BY_PARTNER)) {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_BOTH);
            } else {
                partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
            }
            BankTransferPaymentMethod bankTransferPaymentMethod = new BankTransferPaymentMethod();
            bankTransferPaymentMethod.setAmount(new Price(request.amount().amount(), Currency.getInstance(request.amount().currency())));
            partnerCommission.setPaymentMethod(bankTransferPaymentMethod);
            partnerCommission.setPaymentDirection(PaymentDirection.FOODEALS_TO_PARTENER);
            this.partnerCommissionsRepository.save(partnerCommission);
        } catch(Exception e) {
            throw new RuntimeException("error : payment not validated");
        }
        return new PaymentResponse("payment validated successfully");
    }

}