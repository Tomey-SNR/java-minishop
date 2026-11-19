package com.codecool.minishop.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false)
    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(
            Product product,
            String productName,
            BigDecimal purchasePrice,
            Integer quantity) {
        this.product = product;
        this.productName = productName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }
}
