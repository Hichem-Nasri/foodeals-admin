package net.foodeals.payment.application.services.utils.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.request.paymentDetails.ChequeDetails;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.paymentMethods.ChequePaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ChequeProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository repository;

    @Override
    public PaymentResponse process(PaymentRequest request, MultipartFile document, PaymentType type) throws BadRequestException {
        if (document.isEmpty()) {
            throw new BadRequestException("bad document");
        }
        try {
            // upload document
            if (type.equals(PaymentType.COMMISSION)) {
                this.processCommission(request);
            } else {
                this.processSubscription(request);
            }
        } catch(Exception e) {
            throw new RuntimeException("error : " + e.getMessage());
        }
        return new PaymentResponse("payment validated successfully");

    }

    @Override
    public void processCommission(PaymentRequest request) {
        PartnerCommissions partnerCommission = this.repository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException(" not found "));
        ChequePaymentMethod chequePaymentMethod = new ChequePaymentMethod();
        ChequeDetails chequeDetails = (ChequeDetails) request.paymentDetails();
        chequePaymentMethod.setChequeNumber(chequeDetails.chequeNumber());
        chequePaymentMethod.setDeadlineDate(chequeDetails.deadlineDate());
        chequePaymentMethod.setRecuperationDate(chequeDetails.recuperationDate());
        chequePaymentMethod.setBank(chequeDetails.bankName());
        chequePaymentMethod.setIssuer(chequeDetails.issuer());
        chequePaymentMethod.setAmount(new Price(request.amount().amount(), Currency.getInstance(request.amount().currency())));
        partnerCommission.setPaymentMethod(chequePaymentMethod);
        partnerCommission.setPaymentDirection(PaymentDirection.FOODEALS_TO_PARTENER);
        //            chequePaymentMethod.setChequeDocument("")
        partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
        if (partnerCommission.getPartnerInfo().type().equals(PartnerType.PARTNER_SB)) {
            Set<PartnerCommissions> childs = partnerCommission.getSubEntityCommissions();
            childs = childs.stream().map(c -> {
                c.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
                return c;
            }).collect(Collectors.toSet());
            this.repository.saveAll(childs);
        }
        this.repository.save(partnerCommission);
    }
    @Override
    public void processSubscription(PaymentRequest request) {

    }

}
