package com.codecool.minishop.service;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.model.OrderItem;
import com.codecool.minishop.model.OrderStatus;
import com.codecool.minishop.model.Product;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.OrderRepository;
import com.codecool.minishop.repository.ProductRepository;
import com.codecool.minishop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order purchaseProduct(String username, Long productId, int quantity) {

        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Purchase quantity must be at least 1."
            );
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found.")
                );

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found.")
                );

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough stock available."
            );
        }

        BigDecimal totalAmount = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        Order order = new Order(
                user,
                totalAmount,
                OrderStatus.COMPLETED
        );

        OrderItem orderItem = new OrderItem(
                product,
                product.getName(),
                product.getPrice(),
                quantity
        );

        order.addItem(orderItem);
        user.addOrder(order);

        product.setStockQuantity(
                product.getStockQuantity() - quantity
        );

        productRepository.save(product);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found.")
                );

        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
}
