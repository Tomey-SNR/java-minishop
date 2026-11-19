package com.codecool.minishop.controller;

import com.codecool.minishop.dto.UserResponse;
import com.codecool.minishop.model.Order;
import com.codecool.minishop.service.OrderService;
import com.codecool.minishop.service.ProductResetService;
import com.codecool.minishop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductResetService productResetService;
    private final OrderService orderService;

    public AdminController(
            UserService userService,
            ProductResetService productResetService,
            OrderService orderService) {
        this.userService = userService;
        this.productResetService = productResetService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<UserResponse> users = userService.getAllUserResponses();

        model.addAttribute("users", users);

        return "admin/users";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("orders", orders);

        return "admin/orders";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id, authentication.getName());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account deleted successfully."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/products/reset")
    public String resetProductCatalog(
            RedirectAttributes redirectAttributes) {

        try {
            productResetService.resetProductCatalog();

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Product catalog has been reset successfully."
            );

        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to reset the product catalog."
            );
        }

        return "redirect:/admin/users";
    }
}
