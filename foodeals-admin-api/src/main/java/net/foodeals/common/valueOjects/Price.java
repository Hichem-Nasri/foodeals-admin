package net.foodeals.common.valueOjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
public record Price(BigDecimal amount, Currency currency) {

    public static Price ZERO(Currency currency) {
        return new Price(BigDecimal.ZERO, currency);
    }

    public static Price add(Price price1, Price price2) {
        if (!price1.currency.equals(price2.currency)) {
            throw new IllegalArgumentException("Cannot add prices with different currencies");
        }
        return new Price(
                price1.amount.add(price2.amount),
                price1.currency
        );
    }

    // Add amount only
    public Price add(BigDecimal amount) {
        return new Price(this.amount.add(amount), this.currency);
    }
}