package net.foodeals.payment.domain.entities.paymentMethods;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.payment.domain.entities.PaymentMethod;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@DiscriminatorValue("CHEQUE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChequePaymentMethod extends PaymentMethod {

    private String chequeNumber;
    private Date deadlineDate;
    private Date recuperationDate;
    private String bank;
    private String issuer;
    private byte[] chequeDocument;
}
