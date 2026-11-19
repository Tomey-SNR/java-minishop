package com.codecool.minishop.controller;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/purchase/{productId}")
    public String purchaseProduct(
            @PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Order order = orderService.purchaseProduct(
                    authentication.getName(),
                    productId,
                    quantity
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Purchase completed successfully. Order number: "
                            + order.getId()
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/products/" + productId;
    }

    @GetMapping
    public String getUserOrders(
            Authentication authentication,
            Model model) {

        List<Order> orders = orderService.getUserOrders(
                authentication.getName()
        );

        model.addAttribute("orders", orders);

        return "orders/history";
    }
}
