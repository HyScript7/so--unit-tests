package com.example.eshop.order;

import com.example.eshop.cart.Cart;
import com.example.eshop.cart.CartItem;
import com.example.eshop.payment.CreditCardPaymentProcessor;
import com.example.eshop.payment.PaymentProcessor;
import com.example.eshop.product.DigitalProduct;
import com.example.eshop.product.PhysicalProduct;
import com.example.eshop.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class OrderProkopTest {
    @Test
    void testPlaceOrder_WithMultipleProductsContainsAll_Success_ProkopTest() {
        Product productPhysical = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Product productDigital = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
        Cart cart = new Cart();
        // Order matters!
        cart.addItem(productPhysical, 2);
        cart.addItem(productDigital, 1);

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        AtomicReference<Order> order = new AtomicReference<>();
        assertDoesNotThrow(() -> order.set(orderService.placeOrder(cart)));
        assertNotNull(order.get());
        List<Product> cartProducts = order.get().getItems().stream().map(CartItem::getProduct).toList();
        List<Integer> cartQuantities = order.get().getItems().stream().map(CartItem::getQuantity).toList();
        assertEquals(cartProducts.get(0), productPhysical);
        assertEquals(cartProducts.get(1), productDigital);
        assertEquals(2, (int) cartQuantities.get(0));
        assertEquals(1, (int) cartQuantities.get(1));
    }

    @Test
    void testPlaceOrder_WithValidCartAndValidPayment_StatusPaid_ProkopTest() {
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
    void testPlaceOrder_HasUUID_Success_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        AtomicReference<Order> order = new AtomicReference<>();
        assertDoesNotThrow(() -> order.set(orderService.placeOrder(cart)));
        assertNotNull(order.get());
        assertNotNull(order.get().getId());
    }

    @Test
    void testPlaceOrder_HasDate_Success_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        AtomicReference<Order> order = new AtomicReference<>();
        assertDoesNotThrow(() -> order.set(orderService.placeOrder(cart)));
        assertNotNull(order.get());
        assertNotNull(order.get().getOrderDate());
    }

    @Test
    void testPlaceOrder_WithEmptyCartAndValidPayment_Fails_ProkopTest() {
        Cart cart = new Cart();

        OrderService orderService = new OrderService(new CreditCardPaymentProcessor());
        assertThrowsExactly(IllegalStateException.class, () -> orderService.placeOrder(cart));
    }

    @Test
    void testPlaceOrder_WithValidCardAndInvalidPayment_StatusCancelled_ProkopTest() {
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
}
