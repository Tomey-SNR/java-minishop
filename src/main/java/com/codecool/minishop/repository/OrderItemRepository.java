package com.codecool.minishop.repository;

import com.codecool.minishop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderItem oi SET oi.product = null WHERE oi.product IS NOT NULL")
    int detachProductsFromOrderItems();
}
