package net.foodeals.payment.domain.entities.paymentMethods;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.foodeals.payment.domain.entities.PaymentMethod;

import java.time.LocalDate;
import java.util.Date;

@Entity
@DiscriminatorValue("BANK_TRANSFER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransferPaymentMethod extends PaymentMethod {

    private String documentPath;
    private Date payedAt;

    @Override
    public Date getOperationDate() {
        return this.payedAt;
    }

    @Override
    public String getDocumentPath() {
        return this.documentPath;
    }
}
