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

    @Test
    void shouldFindProductsByPartialNameIgnoringCase() {
        productRepository.save(createProduct());

        productRepository.save(new Product(
                "Gaming Headset",
                "Wireless gaming headset",
                new BigDecimal("99.99"),
                10
        ));

        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("59.99"),
                15
        ));

        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("HEAD");

        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(product -> product.getName().toLowerCase().contains("head")));
    }

    @Test
    void shouldReturnEmptyListWhenNoProductMatchesSearch() {
        productRepository.save(createProduct());

        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("Smartphone");

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldFindProductsWithinPriceRange() {
        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("59.99"),
                15
        ));

        productRepository.save(createProduct());

        productRepository.save(new Product(
                "Gaming Monitor",
                "27-inch gaming monitor",
                new BigDecimal("199.99"),
                8
        ));

        List<Product> results = productRepository.findByPriceBetween(
                new BigDecimal("50.00"),
                new BigDecimal("100.00")
        );

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(product ->
                product.getPrice().compareTo(new BigDecimal("50.00")) >= 0
                        && product.getPrice().compareTo(new BigDecimal("100.00")) <= 0
        ));
    }

    @Test
    void shouldFindProductsAtPriceRangeBoundaries() {
        productRepository.save(new Product(
                "Budget Mouse",
                "Wireless mouse",
                new BigDecimal("20.00"),
                10
        ));

        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("50.00"),
                15
        ));

        productRepository.save(new Product(
                "Headphones",
                "Bluetooth headphones",
                new BigDecimal("100.00"),
                20
        ));

        List<Product> results = productRepository.findByPriceBetween(
                new BigDecimal("50.00"),
                new BigDecimal("100.00")
        );

        assertEquals(2, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatchPriceRange() {
        productRepository.save(createProduct());

        List<Product> results = productRepository.findByPriceBetween(
                new BigDecimal("200.00"),
                new BigDecimal("500.00")
        );

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldFindProductsByNameAndPriceRange() {
        Product affordableHeadset = new Product(
                "Gaming Headset",
                "Affordable headset",
                new BigDecimal("50.00"),
                5
        );

        Product expensiveHeadset = new Product(
                "Premium Headset",
                "Expensive headset",
                new BigDecimal("150.00"),
                3
        );

        Product affordableKeyboard = new Product(
                "Gaming Keyboard",
                "Affordable keyboard",
                new BigDecimal("60.00"),
                4
        );

        productRepository.saveAll(List.of(
                affordableHeadset,
                expensiveHeadset,
                affordableKeyboard
        ));

        List<Product> results =
                productRepository.findByNameContainingIgnoreCaseAndPriceBetween(
                        "headset",
                        new BigDecimal("40.00"),
                        new BigDecimal("100.00")
                );

        assertEquals(1, results.size());
        assertEquals("Gaming Headset", results.get(0).getName());
    }
}
