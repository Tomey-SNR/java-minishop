package com.codecool.minishop.service;

import com.codecool.minishop.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DefaultProductCatalog {

    public List<Product> createProducts() {
        return List.of(
                new Product(
                        "Wireless Headphones",
                        "Comfortable wireless headphones with clear sound.",
                        new BigDecimal("59.99"),
                        25
                ),
                new Product(
                        "Mechanical Keyboard",
                        "Mechanical keyboard suitable for everyday use.",
                        new BigDecimal("89.99"),
                        15
                ),
                new Product(
                        "Gaming Mouse",
                        "Ergonomic gaming mouse with adjustable sensitivity.",
                        new BigDecimal("34.99"),
                        30
                ),
                new Product(
                        "USB-C Hub",
                        "Multiport USB-C hub for laptops and computers.",
                        new BigDecimal("29.99"),
                        20
                ),
                new Product(
                        "Laptop Stand",
                        "Adjustable stand for improved laptop positioning.",
                        new BigDecimal("39.99"),
                        12
                ),
                new Product(
                        "Webcam",
                        "Full HD webcam for video calls and meetings.",
                        new BigDecimal("49.99"),
                        18
                ),
                new Product(
                        "External SSD",
                        "Portable external solid-state drive.",
                        new BigDecimal("119.99"),
                        10
                ),
                new Product(
                        "Bluetooth Speaker",
                        "Portable speaker with wireless connectivity.",
                        new BigDecimal("44.99"),
                        22
                )
        );
    }
}
