package net.foodeals.payment.application.services.utils.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.payment.application.dto.request.PaymentRequest;
import net.foodeals.payment.application.dto.request.PaymentType;
import net.foodeals.payment.application.dto.response.PaymentResponse;
import net.foodeals.payment.application.services.utils.PaymentProcessor;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentDirection;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.paymentMethods.BankTransferPaymentMethod;
import net.foodeals.payment.domain.repository.PartnerCommissionsRepository;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Currency;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BankTransferProcessor implements PaymentProcessor {

    private final PartnerCommissionsRepository partnerCommissionsRepository;
    private final UserService userService;

    @Override
    @Transactional
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
    // normal : ok
    // sub entity : check for parent
    // parent : check for sub entities;

    @Override
    @Transactional
    public void processCommission(PaymentRequest request) {
        PartnerCommissions partnerCommission = this.partnerCommissionsRepository.findById(request.id()).orElseThrow(() -> new ResourceNotFoundException("commission not found with id : " + request.id().toString()));
        try {
            BankTransferPaymentMethod bankTransferPaymentMethod = new BankTransferPaymentMethod();
            final String email = SecurityContextHolder.getContext().getAuthentication().getName();
            final User emitter = this.userService.findByEmail(email);
            bankTransferPaymentMethod.setPayedAt(new Date());
            partnerCommission.setEmitter(emitter);
            bankTransferPaymentMethod.setAmount(new Price(request.amount().amount(), Currency.getInstance(request.amount().currency())));
            partnerCommission.setPaymentMethod(bankTransferPaymentMethod);
            partnerCommission.setPaymentDirection(PaymentDirection.FOODEALS_TO_PARTENER);
            partnerCommission.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
            if (partnerCommission.getPartnerInfo().type().equals(PartnerType.PARTNER_SB)) {
                Set<PartnerCommissions> childs = partnerCommission.getSubEntityCommissions();
                childs = childs.stream().map(c -> {
                    c.setPaymentStatus(PaymentStatus.VALIDATED_BY_FOODEALS);
                    return c;
                }).collect(Collectors.toSet());
                this.partnerCommissionsRepository.saveAll(childs);
            }
            this.partnerCommissionsRepository.save(partnerCommission);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    @Transactional
    public void processSubscription(PaymentRequest request) {

    }

}