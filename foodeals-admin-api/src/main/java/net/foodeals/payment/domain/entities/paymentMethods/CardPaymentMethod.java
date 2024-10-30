package net.foodeals.payment.domain.entities.paymentMethods;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.foodeals.payment.domain.entities.PaymentMethod;

import java.util.Date;

@Entity
@DiscriminatorValue("CARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentMethod extends PaymentMethod {

    private String cardNumber;
    private String cardHolderName;
    private String paymentId;
    private Date payedAt;

    @Override
    public Date getOperationDate() {
        return this.payedAt;
    }

    @Override
    public String getDocumentPath() {
        return null;
    }
}
