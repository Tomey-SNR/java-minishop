package com.codecool.minishop.repository;

import com.codecool.minishop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldSaveProduct() {
        Product product = createProduct();

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Wireless Headphones", savedProduct.getName());
        assertEquals(new BigDecimal("79.99"), savedProduct.getPrice());
    }

    @Test
    void shouldFindProductById() {
        Product product = productRepository.save(createProduct());

        Optional<Product> foundProduct =
                productRepository.findById(product.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
    }

    @Test
    void shouldFindAllProducts() {
        productRepository.save(createProduct());
        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("59.99"),
                15
        ));

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
    }

    @Test
    void shouldDeleteProductById() {
        Product product = productRepository.save(createProduct());

        productRepository.deleteById(product.getId());

        assertFalse(productRepository.existsById(product.getId()));
    }

    @Test
    void shouldCountProducts() {
        productRepository.save(createProduct());
        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("59.99"),
                15
        ));

        assertEquals(2, productRepository.count());
    }

    private Product createProduct() {
        return new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("79.99"),
                25
        );
    }
}
