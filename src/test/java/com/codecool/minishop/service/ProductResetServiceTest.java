package com.codecool.minishop.service;

import com.codecool.minishop.model.Product;
import com.codecool.minishop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductResetServiceTest {

    private ProductRepository productRepository;
    private DefaultProductCatalog defaultProductCatalog;
    private ProductResetService productResetService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        defaultProductCatalog = new DefaultProductCatalog();

        productResetService = new ProductResetService(
                productRepository,
                defaultProductCatalog
        );
    }

    @Test
    void shouldDeleteExistingProductsAndGenerateDefaultCatalog() {
        productResetService.resetProductCatalog();

        verify(productRepository).deleteAllInBatch();

        ArgumentCaptor<Iterable<Product>> productsCaptor =
                ArgumentCaptor.forClass(Iterable.class);

        verify(productRepository).saveAll(productsCaptor.capture());

        List<Product> savedProducts = new ArrayList<>();
        productsCaptor.getValue().forEach(savedProducts::add);

        assertEquals(8, savedProducts.size());

        assertTrue(savedProducts.stream().anyMatch(product ->
                product.getName().equals("Wireless Headphones")
        ));

        assertTrue(savedProducts.stream().anyMatch(product ->
                product.getName().equals("Mechanical Keyboard")
        ));

        assertTrue(savedProducts.stream().anyMatch(product ->
                product.getName().equals("Gaming Mouse")
        ));
    }

    @Test
    void shouldGenerateProductsWithValidPricesAndStock() {
        List<Product> products = defaultProductCatalog.createProducts();

        assertEquals(8, products.size());

        assertTrue(products.stream().allMatch(product ->
                product.getPrice().signum() > 0
                        && product.getStockQuantity() >= 0
        ));
    }

    @Test
    void shouldGenerateNewProductInstancesEveryTime() {
        List<Product> firstCatalog = defaultProductCatalog.createProducts();
        List<Product> secondCatalog = defaultProductCatalog.createProducts();

        assertNotSame(firstCatalog.getFirst(), secondCatalog.getFirst());
    }
}
