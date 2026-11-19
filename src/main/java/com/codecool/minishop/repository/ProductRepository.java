package com.codecool.minishop.repository;

import com.codecool.minishop.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByNameContainingIgnoreCaseAndPriceBetween(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<Product> findAllByOrderByNameAsc();

    List<Product> findAllByOrderByNameDesc();

    List<Product> findAllByOrderByPriceAsc();

    List<Product> findAllByOrderByPriceDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
