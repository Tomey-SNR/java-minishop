package com.codecool.minishop.service;

import com.codecool.minishop.model.Order;
import com.codecool.minishop.model.OrderStatus;
import com.codecool.minishop.model.Product;
import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.OrderRepository;
import com.codecool.minishop.repository.ProductRepository;
import com.codecool.minishop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private OrderService orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        productRepository = mock(ProductRepository.class);
        orderRepository = mock(OrderRepository.class);

        orderService = new OrderService(
                userRepository,
                productRepository,
                orderRepository
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = new User(
                "tomey",
                "tomey@example.com",
                "encoded-password",
                Role.USER
        );

        product = new Product(
                "Wireless Headphones",
                "Bluetooth headphones",
                new BigDecimal("59.99"),
                10
        );

        when(userRepository.findByUsername("tomey"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));
    }

    @Test
    void shouldPurchaseProductSuccessfully() {
        Order order = orderService.purchaseProduct("tomey", 1L, 2);

        assertNotNull(order);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(new BigDecimal("119.98"), order.getTotalAmount());
        assertEquals(8, product.getStockQuantity());

        assertEquals(1, order.getItems().size());
        assertEquals("Wireless Headphones",
                order.getItems().getFirst().getProductName());
        assertEquals(new BigDecimal("59.99"),
                order.getItems().getFirst().getPurchasePrice());
        assertEquals(2, order.getItems().getFirst().getQuantity());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.purchaseProduct("tomey", 1L, 0)
        );

        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.purchaseProduct("tomey", 1L, -1)
        );

        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }

    @Test
    void shouldRejectPurchaseWhenStockIsInsufficient() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.purchaseProduct("tomey", 1L, 11)
        );

        assertEquals(10, product.getStockQuantity());

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldRejectPurchaseWhenProductDoesNotExist() {
        when(productRepository.findByIdForUpdate(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.purchaseProduct("tomey", 99L, 1)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldRejectPurchaseWhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.purchaseProduct("unknown", 1L, 1)
        );

        verify(productRepository, never()).findByIdForUpdate(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
