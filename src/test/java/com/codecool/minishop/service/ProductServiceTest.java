package com.codecool.minishop.service;

import com.codecool.minishop.model.Product;
import com.codecool.minishop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        Product product = createProduct();

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnAllProducts() {
        List<Product> products = List.of(
                createProduct(),
                new Product("Keyboard", "Mechanical keyboard",
                        new BigDecimal("59.99"), 15)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductById() {
        Product product = createProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldUpdateProduct() {
        Product existingProduct = createProduct();
        Product updatedProduct = new Product(
                "Updated Headphones",
                "Updated description",
                new BigDecimal("99.99"),
                10
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertEquals("Updated Headphones", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        assertEquals(10, result.getStockQuantity());

        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        Product updatedProduct = createProduct();

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(99L, updatedProduct)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).deleteById(99L);
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
