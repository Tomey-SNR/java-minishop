package com.codecool.minishop.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private ValidatorFactory factory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        factory.close();
    }

    @Test
    void shouldAcceptValidProduct() {
        Product product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                25
        );

        assertTrue(validator.validate(product).isEmpty());
    }

    @Test
    void shouldRejectBlankProductName() {
        Product product = new Product(
                " ",
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                25
        );

        assertFalse(validator.validate(product).isEmpty());
    }

    @Test
    void shouldRejectProductNameLongerThan100Characters() {
        Product product = new Product(
                "A".repeat(101),
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                25
        );

        assertFalse(validator.validate(product).isEmpty());
    }

    @Test
    void shouldRejectNullPrice() {
        Product product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                null,
                25
        );

        assertFalse(validator.validate(product).isEmpty());
    }

    @Test
    void shouldRejectZeroPrice() {
        Product product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                BigDecimal.ZERO,
                25
        );

        assertFalse(validator.validate(product).isEmpty());
    }

    @Test
    void shouldRejectNegativeStockQuantity() {
        Product product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                -1
        );

        assertFalse(validator.validate(product).isEmpty());
    }

    @Test
    void shouldAcceptZeroStockQuantity() {
        Product product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                0
        );

        assertTrue(validator.validate(product).isEmpty());
    }
}
