package com.example.eshop.cart;

import com.example.eshop.product.DigitalProduct;
import com.example.eshop.product.PhysicalProduct;
import com.example.eshop.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartProkopTest {
    @Test
    void testCreateCart_Empty_Success_ProkopTest() {
        assertDoesNotThrow(Cart::new);
        assertTrue(new Cart().getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_NegativeQuantity_Fail_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertThrowsExactly(IllegalArgumentException.class, () -> cart.addItem(product, -8));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testModifyItemInCart_ToNegativeQuantity_Fail_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertFalse(cart.getItems().isEmpty());
        assertThrowsExactly(IllegalArgumentException.class, () -> cart.getItems().getFirst().setQuantity(-8));
        assertFalse(cart.getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_Duplicate_Success_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertDoesNotThrow(() -> cart.addItem(product, 4));
        assertFalse(cart.getItems().isEmpty());
        assertEquals(2 + 4, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testAddItemToCart_WithPhysicalProduct_Success_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertFalse(cart.getItems().isEmpty());
        assertEquals(product, cart.getItems().getFirst().getProduct());
        assertEquals(2, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testRemoveItemFromCart_WithPhysicalProduct_Success_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);
        assertDoesNotThrow(() -> cart.removeItem(product));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddItemToCart_WithDigitalProduct_Success_ProkopTest() {
        Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
        Cart cart = new Cart();
        assertDoesNotThrow(() -> cart.addItem(product, 2));
        assertFalse(cart.getItems().isEmpty());
        assertEquals(product, cart.getItems().getFirst().getProduct());
        assertEquals(2, cart.getItems().getFirst().getQuantity());
    }

    @Test
    void testRemoveItemFromCart_WithDigitalProduct_Success_ProkopTest() {
        Product product = new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download");
        Cart cart = new Cart();
        cart.addItem(product, 2);
        assertDoesNotThrow(() -> cart.removeItem(product));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testCalculateTotal_WithEmptyCart_CorrectTotal_ProkopTest() {
        Cart cart = new Cart();
        BigDecimal expectedTotal = BigDecimal.valueOf(0);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testCalculateTotal_WithSingleItem_CorrectTotal_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 1);
        BigDecimal expectedTotal = BigDecimal.valueOf(20.00d);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testCalculateTotal_WithMultipleItems_CorrectTotal_ProkopTest() {
        Product product = new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5));
        Cart cart = new Cart();
        cart.addItem(product, 2);
        BigDecimal expectedTotal = BigDecimal.valueOf(40.00d);
        BigDecimal actualTotal = cart.calculateTotal();
        assertEquals(expectedTotal, actualTotal);
    }
}
