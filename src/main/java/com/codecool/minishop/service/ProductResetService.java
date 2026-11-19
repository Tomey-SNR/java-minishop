package com.codecool.minishop.service;

import com.codecool.minishop.model.Product;
import com.codecool.minishop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductResetService {

    private final ProductRepository productRepository;
    private final DefaultProductCatalog defaultProductCatalog;

    public ProductResetService(
            ProductRepository productRepository,
            DefaultProductCatalog defaultProductCatalog) {
        this.productRepository = productRepository;
        this.defaultProductCatalog = defaultProductCatalog;
    }

    @Transactional
    public void resetProductCatalog() {
        productRepository.deleteAllInBatch();

        List<Product> defaultProducts =
                defaultProductCatalog.createProducts();

        productRepository.saveAll(defaultProducts);
    }
}
