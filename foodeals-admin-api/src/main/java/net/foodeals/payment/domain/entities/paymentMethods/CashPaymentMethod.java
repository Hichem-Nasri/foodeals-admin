package net.foodeals.payment.domain.entities.paymentMethods;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.foodeals.payment.domain.entities.PaymentMethod;

import java.util.Date;

@Entity
@DiscriminatorValue("CASH")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentMethod extends PaymentMethod {
    private Date recuperationDate;
}
