package net.foodeals.payment.application.services.utils.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.paymentDetails.BankTransferDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
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

@Component
@AllArgsConstructor
public class BankTransferProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository partnerCommissionsRepository;

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document) throws BadRequestException {

        if (document.isEmpty()) {
            throw new BadRequestException("bad document");
        }

        // logic to upload file into cloud storage.

        PartnerCommissions partnerCommissions = this.partnerCommissionsRepository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id : " + request.id().toString() ));
        partnerCommissions.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
        BankTransferPaymentMethod bankTransferPaymentMethod = new BankTransferPaymentMethod();
        // saving document path
        // bankTransferPaymentMethod.setDocumentPath("")
        bankTransferPaymentMethod.setAmount(request.amount());

        partnerCommissions.setPaymentMethod(bankTransferPaymentMethod);
        this.partnerCommissionsRepository.save(partnerCommissions);
        return new PaymentResponse("payment validated successfully");
    }

}