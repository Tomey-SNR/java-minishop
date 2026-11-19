package com.codecool.minishop.controller;

import com.codecool.minishop.service.ProductResetService;
import com.codecool.minishop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    private ProductResetService productResetService;
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        UserService userService = mock(UserService.class);
        productResetService = mock(ProductResetService.class);

        adminController = new AdminController(
                userService,
                productResetService
        );
    }

    @Test
    void shouldResetProductCatalogSuccessfully() {
        RedirectAttributes redirectAttributes =
                new RedirectAttributesModelMap();

        String result = adminController.resetProductCatalog(
                redirectAttributes
        );

        verify(productResetService).resetProductCatalog();

        assertEquals("redirect:/admin/users", result);
        assertEquals(
                "Product catalog has been reset successfully.",
                redirectAttributes.getFlashAttributes().get("successMessage")
        );
    }

    @Test
    void shouldDisplayErrorWhenProductResetFails() {
        doThrow(new RuntimeException("Database error"))
                .when(productResetService)
                .resetProductCatalog();

        RedirectAttributes redirectAttributes =
                new RedirectAttributesModelMap();

        String result = adminController.resetProductCatalog(
                redirectAttributes
        );

        assertEquals("redirect:/admin/users", result);
        assertEquals(
                "Failed to reset the product catalog.",
                redirectAttributes.getFlashAttributes().get("errorMessage")
        );
    }
}