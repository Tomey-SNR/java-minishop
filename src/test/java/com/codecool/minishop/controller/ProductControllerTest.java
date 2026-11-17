package com.codecool.minishop.controller;

import com.codecool.minishop.model.Product;
import com.codecool.minishop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldDisplayProductList() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(createProduct()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));

        verify(productService).getAllProducts();
    }

    @Test
    void shouldDisplayProductDetails() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(Optional.of(createProduct()));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/details"))
                .andExpect(model().attributeExists("product"));

        verify(productService).getProductById(1L);
    }

    @Test
    void shouldDisplayCreateProductForm() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .param("name", "Wireless Headphones")
                        .param("description", "Bluetooth headphones")
                        .param("price", "79.99")
                        .param("stockQuantity", "25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void shouldReturnFormWhenProductValidationFails() throws Exception {
        mockMvc.perform(post("/products")
                        .param("name", "")
                        .param("description", "Bluetooth headphones")
                        .param("price", "79.99")
                        .param("stockQuantity", "25"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeHasFieldErrors("product", "name"));

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void shouldDisplayEditProductForm() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(Optional.of(createProduct()));

        mockMvc.perform(get("/products/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("product"));

        verify(productService).getProductById(1L);
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        mockMvc.perform(post("/products/1")
                        .param("name", "Updated Headphones")
                        .param("description", "Updated description")
                        .param("price", "99.99")
                        .param("stockQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(post("/products/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).deleteProduct(1L);
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
