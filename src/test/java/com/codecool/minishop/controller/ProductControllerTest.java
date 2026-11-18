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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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

    @Test
    void shouldSearchProductsByName() throws Exception {
        when(productService.searchProducts("head"))
                .thenReturn(List.of(createProduct()));

        mockMvc.perform(get("/products")
                        .param("search", "head"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("search", "head"));

        verify(productService).searchProducts("head");
        verify(productService, never()).getAllProducts();
    }

    @Test
    void shouldDisplayAllProductsWhenSearchIsMissing() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(createProduct()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));

        verify(productService).getAllProducts();
        verify(productService, never()).searchProducts(anyString());
    }

    @Test
    void shouldHandleSearchWithoutResults() throws Exception {
        when(productService.searchProducts("phone"))
                .thenReturn(List.of());

        mockMvc.perform(get("/products")
                        .param("search", "phone"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("search", "phone"));

        verify(productService).searchProducts("phone");
    }

    @Test
    void shouldFilterProductsByPriceRange() throws Exception {
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        Product product = createProduct();
        when(productService.filterProductsByPrice(minPrice, maxPrice))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/products")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", List.of(product)))
                .andExpect(model().attribute("minPrice", minPrice))
                .andExpect(model().attribute("maxPrice", maxPrice));

        verify(productService).filterProductsByPrice(minPrice, maxPrice);
        verify(productService, never()).getAllProducts();
    }

    @Test
    void shouldDisplayAllProductsWhenPriceRangeIsIncomplete() throws Exception {
        List<Product> products = List.of(createProduct());

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products")
                        .param("minPrice", "50.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", products));

        verify(productService).getAllProducts();
        verify(productService, never())
                .filterProductsByPrice(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void shouldHandlePriceFilterWithoutResults() throws Exception {
        BigDecimal minPrice = new BigDecimal("500.00");
        BigDecimal maxPrice = new BigDecimal("1000.00");

        when(productService.filterProductsByPrice(minPrice, maxPrice))
                .thenReturn(List.of());

        mockMvc.perform(get("/products")
                        .param("minPrice", "500.00")
                        .param("maxPrice", "1000.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", List.of()))
                .andExpect(model().attribute("minPrice", minPrice))
                .andExpect(model().attribute("maxPrice", maxPrice));

        verify(productService).filterProductsByPrice(minPrice, maxPrice);
    }

    @Test
    void shouldHandleInvalidPriceRange() throws Exception {
        mockMvc.perform(get("/products")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "50.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute(
                        "priceError",
                        "Minimum price cannot be greater than maximum price."
                ));

        verify(productService, never())
                .filterProductsByPrice(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void shouldDisplayPriceErrorMessageOnProductPage() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "50.00"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Minimum price cannot be greater than maximum price."
                )));
    }

    @Test
    void shouldSearchProductsByNameAndPriceRange() throws Exception {
        BigDecimal minPrice = new BigDecimal("40.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        Product product = createProduct();

        when(productService.searchProductsByNameAndPrice(
                "headset", minPrice, maxPrice
        )).thenReturn(List.of(product));

        mockMvc.perform(get("/products")
                        .param("search", "headset")
                        .param("minPrice", "40.00")
                        .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", List.of(product)));

        verify(productService).searchProductsByNameAndPrice(
                "headset", minPrice, maxPrice
        );
    }

    @Test
    void shouldHandleCombinedSearchWithoutResults() throws Exception {
        when(productService.searchProductsByNameAndPrice(
                "headset",
                new BigDecimal("40.00"),
                new BigDecimal("100.00")
        )).thenReturn(List.of());

        mockMvc.perform(get("/products")
                        .param("search", "headset")
                        .param("minPrice", "40.00")
                        .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", List.of()));
    }

    @Test
    void shouldRejectInvalidPriceRangeEvenWhenSearchingByName() throws Exception {
        mockMvc.perform(get("/products")
                        .param("search", "headset")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "50.00"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "priceError",
                        "Minimum price cannot be greater than maximum price."
                ));

        verify(productService, never())
                .searchProductsByNameAndPrice(
                        anyString(),
                        any(BigDecimal.class),
                        any(BigDecimal.class)
                );
    }
}
