package com.codecool.minishop.controller;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        authentication = mock(Authentication.class);

        orderController = new OrderController(orderService);

        when(authentication.getName()).thenReturn("tomey");
    }

    @Test
    void shouldPurchaseProductSuccessfully() {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(15L);

        when(orderService.purchaseProduct("tomey", 1L, 2))
                .thenReturn(order);

        RedirectAttributes redirectAttributes =
                new RedirectAttributesModelMap();

        String result = orderController.purchaseProduct(
                1L,
                2,
                authentication,
                redirectAttributes
        );

        assertEquals("redirect:/products/1", result);

        assertEquals(
                "Purchase completed successfully. Order number: 15",
                redirectAttributes.getFlashAttributes().get("successMessage")
        );

        verify(orderService).purchaseProduct("tomey", 1L, 2);
    }

    @Test
    void shouldDisplayErrorWhenPurchaseFails() {
        when(orderService.purchaseProduct("tomey", 1L, 5))
                .thenThrow(new IllegalArgumentException(
                        "Not enough stock available."
                ));

        RedirectAttributes redirectAttributes =
                new RedirectAttributesModelMap();

        String result = orderController.purchaseProduct(
                1L,
                5,
                authentication,
                redirectAttributes
        );

        assertEquals("redirect:/products/1", result);

        assertEquals(
                "Not enough stock available.",
                redirectAttributes.getFlashAttributes().get("errorMessage")
        );
    }

    @Test
    void shouldDisplayUserOrderHistory() {
        List<Order> orders = List.of(
                mock(Order.class),
                mock(Order.class)
        );

        when(orderService.getUserOrders("tomey"))
                .thenReturn(orders);

        Model model = new ExtendedModelMap();

        String result = orderController.getUserOrders(
                authentication,
                model
        );

        assertEquals("orders/history", result);
        assertEquals(orders, model.getAttribute("orders"));

        verify(orderService).getUserOrders("tomey");
    }
}
