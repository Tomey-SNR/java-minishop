package com.codecool.minishop.service;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.model.OrderItem;
import com.codecool.minishop.model.OrderStatus;
import com.codecool.minishop.model.Product;
import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.OrderItemRepository;
import com.codecool.minishop.repository.OrderRepository;
import com.codecool.minishop.repository.ProductRepository;
import com.codecool.minishop.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDeletionIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldDeleteUserOrdersAndOrderItemsButPreserveProductsAndOtherUsersData() {

        User userToDelete = userRepository.saveAndFlush(new User(
                "delete-me",
                "delete-me@example.com",
                "encoded-password",
                Role.USER
        ));

        User otherUser = userRepository.saveAndFlush(new User(
                "keep-me",
                "keep-me@example.com",
                "encoded-password",
                Role.USER
        ));

        Product product = productRepository.saveAndFlush(new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("59.99"),
                25
        ));

        Order deletedUsersOrder = new Order(
                userToDelete,
                new BigDecimal("119.98"),
                OrderStatus.COMPLETED
        );

        deletedUsersOrder.addItem(new OrderItem(
                product,
                product.getName(),
                product.getPrice(),
                2
        ));

        userToDelete.addOrder(deletedUsersOrder);
        orderRepository.save(deletedUsersOrder);

        Order otherUsersOrder = new Order(
                otherUser,
                new BigDecimal("59.99"),
                OrderStatus.COMPLETED
        );

        otherUsersOrder.addItem(new OrderItem(
                product,
                product.getName(),
                product.getPrice(),
                1
        ));

        otherUser.addOrder(otherUsersOrder);
        orderRepository.save(otherUsersOrder);

        Long deletedUserId = userToDelete.getId();
        Long otherUserId = otherUser.getId();
        Long productId = product.getId();

        entityManager.flush();

        userService.deleteUser(deletedUserId, "admin");

        entityManager.flush();
        entityManager.clear();

        assertFalse(userRepository.existsById(deletedUserId));
        assertTrue(userRepository.existsById(otherUserId));
        assertEquals(1, orderRepository.count());
        assertEquals(1, orderItemRepository.count());
        assertTrue(productRepository.existsById(productId));

        User remainingUser = userRepository.findById(otherUserId)
                .orElseThrow();

        assertEquals(
                1,
                orderRepository.findByUserOrderByOrderDateDesc(remainingUser).size()
        );
    }
}
