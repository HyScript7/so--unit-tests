package com.example.eshop;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.example.eshop.cart.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        assertTrue(new Cart().getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_NegativeQuantity_Fail() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertThrowsExactly(IllegalArgumentException.class, () -> cart.addItem(product, -8));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testModifyItemInCart_ToNegativeQuantity_Fail() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertFalse(cart.getItems().isEmpty());
        assertThrowsExactly(IllegalArgumentException.class, () -> cart.getItems().getFirst().setQuantity(-8));
        assertFalse(cart.getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_Duplicate_Success() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertDoesNotThrow(() -> cart.addItem(product, 4));
        assertFalse(cart.getItems().isEmpty());
        assertEquals(2 + 4, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testAddItemToCart_WithPhysicalProduct_Success() {
            Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
            Cart cart = new Cart();
            assertDoesNotThrow(() -> cart.addItem(product, 2));
            assertFalse(cart.getItems().isEmpty());
            assertEquals(product, cart.getItems().getFirst().getProduct());
            assertEquals(2, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testRemoveItemFromCart_WithPhysicalProduct_Success() {
            Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
            Cart cart = new Cart();
            cart.addItem(product, 2);
            assertDoesNotThrow(() -> cart.removeItem(product));
            assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_WithDigitalProduct_Success() {
            Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
            Cart cart = new Cart();
            assertDoesNotThrow(() -> cart.addItem(product, 2));
            assertFalse(cart.getItems().isEmpty());
            assertEquals(product, cart.getItems().getFirst().getProduct());
            assertEquals(2, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testRemoveItemFromCart_WithDigitalProduct_Success() {
            Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
            Cart cart = new Cart();
            cart.addItem(product, 2);
            assertDoesNotThrow(() -> cart.removeItem(product));
            assertTrue(cart.getItems().isEmpty());
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
    void testPlaceOrder_WithMultipleProductsContainsAll_Success() {
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
    void testPlaceOrder_HasUUID_Success() {
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
    @ValueSource(doubles = { 10.0, 20.0, 35.0 })
    void testProcessPayment_WithPositiveAmount_True(double amount) {
        assertTrue(new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(amount)));
    }

    @Test
    void testProcessPayment_WithNegativeAmount_False() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new CreditCardPaymentProcessor().processPayment(BigDecimal.valueOf(-10.0d)));
    }
}
