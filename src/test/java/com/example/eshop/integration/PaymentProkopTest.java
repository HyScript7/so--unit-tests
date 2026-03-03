package com.example.eshop.integration;

import com.example.eshop.payment.CreditCardPaymentProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentProkopTest {
    @ParameterizedTest
    @ValueSource(doubles = { 10.0, 20.0, 35.0 })
    void testProcessPayment_WithPositiveAmount_True_ProkopTest(double amount) {
        assertTrue(new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(amount)));
    }

    @Test
    void testProcessPayment_WithNegativeAmount_False_ProkopTest() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(-10.0d)));
    }
}
