package com.example.eshop.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductProkopTest {
    @Test
    void testCreatePhysicalProduct_WithValidData_Success_ProkopTest() {
        AtomicReference<PhysicalProduct> productRef = new AtomicReference<>();
        assertDoesNotThrow(() -> productRef.set(new PhysicalProduct("Test Physical Product", "An example physical product", BigDecimal.valueOf(20.00d), 10, BigDecimal.valueOf(2.5))));
        PhysicalProduct product = productRef.get();
        assertAll("Product Attributes Match",
                () -> assertEquals("Test Physical Product", product.getName()),
                () -> assertEquals("An example physical product", product.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(20.00d), product.getPrice()),
                () -> assertEquals(10, product.getWeight()),
                () -> assertEquals(BigDecimal.valueOf(2.5), product.getShippingCost()),
                () -> assertNotNull(product.getId())
        );
        var productHashBeforeUpdate = product.hashCode();
        assertAll("Product Can Be Updated",
                () -> assertDoesNotThrow(() -> product.setWeight(9.9)),
                () -> assertDoesNotThrow(() -> product.setDescription("Updated")),
                () -> assertDoesNotThrow(() -> product.setPrice(BigDecimal.valueOf(69.420))),
                () -> assertDoesNotThrow(() -> product.setShippingCost(BigDecimal.valueOf(69.420))),
                () -> assertDoesNotThrow(() -> product.setName("Renamed"))
        );
        assertAll("Product Updates Persist",
                () -> assertEquals(9.9, product.getWeight()),
                () -> assertEquals("Updated", product.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(69.420), product.getPrice()),
                () -> assertEquals(BigDecimal.valueOf(69.420), product.getShippingCost()),
                () -> assertEquals("Renamed", product.getName())
        );
        assertNotEquals(new PhysicalProduct("a", "a", BigDecimal.TEN, 9.9, BigDecimal.ONE), product);
        assertEquals(productHashBeforeUpdate, product.hashCode());
    }

    @Test
    void testCreateDigitalProduct_WithValidData_Success_ProkopTest() {
        AtomicReference<DigitalProduct> productRef = new AtomicReference<>();
        assertDoesNotThrow(() -> productRef.set(new DigitalProduct("Test Digital Product", "An example digital product", BigDecimal.valueOf(10.00d), "http://example.com/download")));
        DigitalProduct product = productRef.get();
        assertAll("Digital Product Attributes Match",
                () -> assertEquals("Test Digital Product", product.getName()),
                () -> assertEquals("An example digital product", product.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(10.00d), product.getPrice()),
                () -> assertEquals("http://example.com/download", product.getDownloadUrl())
        );
        var productHashBeforeUpdate = product.hashCode();
        assertAll("Product Can Be Updated",
                () -> assertDoesNotThrow(() -> product.setDownloadUrl(null)),
                () -> assertDoesNotThrow(() -> product.setDescription("Updated")),
                () -> assertDoesNotThrow(() -> product.setPrice(BigDecimal.valueOf(69.420))),
                () -> assertDoesNotThrow(() -> product.setName("Renamed"))
        );
        assertAll("Product Updates Persist",
                () -> assertNull(product.getDownloadUrl()),
                () -> assertEquals("Updated", product.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(69.420), product.getPrice()),
                () -> assertEquals("Renamed", product.getName())
        );
        assertNotEquals(new DigitalProduct("Renamed", "Updated", product.getPrice(), null), product);
        assertEquals(productHashBeforeUpdate, product.hashCode());
    }
}
