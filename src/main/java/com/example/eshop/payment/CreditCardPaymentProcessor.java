package com.example.eshop.payment;

import java.math.BigDecimal;

public class CreditCardPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Refusing to process negative payment");
        }
        System.out.println("Processing credit card payment of $" + amount);
        return true;
    }
}
