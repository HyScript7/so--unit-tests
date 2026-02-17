package com.example.eshop;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.eshop.cart.Cart;
import com.example.eshop.order.Order;
import com.example.eshop.order.OrderService;
import com.example.eshop.order.OrderStatus;
import com.example.eshop.payment.CreditCardPaymentProcessor;
import com.example.eshop.payment.PaymentProcessor;
import com.example.eshop.product.DigitalProduct;
import com.example.eshop.product.PhysicalProduct;
import com.example.eshop.product.Product;

/**
 * Unit test for simple App.
 */
class AppTest {
    @Test
    void testCreatePhysicalProduct_WithValidData_Success() {
        AtomicReference<PhysicalProduct> productRef = new AtomicReference<>();
        assertDoesNotThrow(() -> productRef.set(new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5))));
        PhysicalProduct product = productRef.get();
        assertAll("Product Attributes Match", 
            () -> assertEquals("Test Physical Product", product.getName()),
            () -> assertEquals("An example physical product", product.getDescription()),
            () -> assertEquals(BigDecimal.valueOf(20.00d), product.getPrice()),
            () -> assertEquals(10, product.getWeight()),
            () -> assertEquals(BigDecimal.valueOf(2.5), product.getShippingCost())
        );
    }

    @Test
    void testCreateDigitalProduct_WithValidData_Success() {
        AtomicReference<DigitalProduct> productRef = new AtomicReference<>();
        assertDoesNotThrow(() -> productRef.set(new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download")));
        DigitalProduct product = productRef.get();
        assertAll("Digital Product Attributes Match",
            () -> assertEquals("Test Digital Product", product.getName()),
            () -> assertEquals("An example digital product", product.getDescription()),
            () -> assertEquals(BigDecimal.valueOf(10.00d), product.getPrice()),
            () -> assertEquals("http://example.com/download", product.getDownloadUrl())
        );
    }

    @Test
    void testCreateCart_Empty_Success() {
        assertDoesNotThrow(Cart::new);
    }

    @Test
    void testAddItemToCart_WithPhysicalProduct_Success() {
            Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
            Cart cart = new Cart();
            assertDoesNotThrow(() -> cart.addItem(product, 2));
    }

    @Test
    void testRemoveItemFromCart_WithPhysicalProduct_Success() {
            Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
            Cart cart = new Cart();
            cart.addItem(product, 2);
            assertDoesNotThrow(() -> cart.removeItem(product));
    }

    @Test
    void testAddItemToCart_WithDigitalProduct_Success() {
            Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
            Cart cart = new Cart();
            assertDoesNotThrow(() -> cart.addItem(product, 2));
    }

    @Test
    void testRemoveItemFromCart_WithDigitalProduct_Success() {
            Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
            Cart cart = new Cart();
            cart.addItem(product, 2);
            assertDoesNotThrow(() -> cart.removeItem(product));
    }

    @Test
    void testCalculateTotal_WithEmptyCart_CorrectTotal() {
        Cart cart = new Cart();
        BigDecimal expectedTotal = BigDecimal.valueOf(0);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testCalculateTotal_WithSingleItem_CorrectTotal() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 1);
        BigDecimal expectedTotal = BigDecimal.valueOf(20.00d);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testCalculateTotal_WithMultipleItems_CorrectTotal() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);
        BigDecimal expectedTotal = BigDecimal.valueOf(40.00d);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testPlaceOrder_WithValidCartAndValidPayment_StatusPaid() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        AtomicReference<Order> order = new AtomicReference<>();
        assertDoesNotThrow(() -> order.set(orderService.placeOrder(cart)));
        assertNotNull(order.get());
        assertEquals(OrderStatus.PAID, order.get().getStatus());
    }

    @Test
    void testPlaceOrder_WithEmptyCartAndValidPayment_Fails() {
        Cart cart = new Cart();

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        assertThrowsExactly(IllegalStateException.class, () -> orderService.placeOrder(cart));
    }

    @Test
    void testPlaceOrder_WithValidCardAndInvalidPayment_StatusCancelled() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);

        OrderService orderService = new OrderService(new PaymentProcessor() {
            @Override
            public boolean processPayment(BigDecimal amount) {
                return false; // Simulate payment failure
            }
        });
        AtomicReference<Order> order = new AtomicReference<>();
        assertDoesNotThrow(() -> order.set(orderService.placeOrder(cart)));
        assertNotNull(order.get());
        assertEquals(OrderStatus.CANCELLED, order.get().getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = { 10.0, 20.0, 35.0 })
    void testProcessPayment_WithPositiveAmount_True(double amount) {
        assertTrue(new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(amount)));
    }

    @Test
    void testProcessPayment_WithNegativeAmount_False() {
        assertFalse(new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(-10.0d)));
    }
}
