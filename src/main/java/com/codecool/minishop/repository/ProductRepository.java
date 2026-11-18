package com.codecool.minishop.repository;

import com.codecool.minishop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByNameContainingIgnoreCaseAndPriceBetween(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

}
