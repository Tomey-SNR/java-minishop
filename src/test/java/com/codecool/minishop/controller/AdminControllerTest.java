package com.codecool.minishop.controller;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.service.OrderService;
import com.codecool.minishop.service.ProductResetService;
import com.codecool.minishop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    private ProductResetService productResetService;
    private OrderService orderService;
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        UserService userService = mock(UserService.class);
        productResetService = mock(ProductResetService.class);
        orderService = mock(OrderService.class);

        adminController = new AdminController(
                userService,
                productResetService,
                orderService
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

    @Test
    void shouldDisplayAllOrdersForAdmin() {
        List<Order> orders = List.of(
                mock(Order.class),
                mock(Order.class)
        );

        when(orderService.getAllOrders()).thenReturn(orders);

        Model model = new ExtendedModelMap();

        String result = adminController.getAllOrders(model);

        assertEquals("admin/orders", result);
        assertEquals(orders, model.getAttribute("orders"));

        verify(orderService).getAllOrders();
    }
}
