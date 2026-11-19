package com.codecool.minishop.config;

import com.codecool.minishop.repository.ProductRepository;
import com.codecool.minishop.service.DefaultProductCatalog;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final DefaultProductCatalog defaultProductCatalog;

    public ProductDataInitializer(
            ProductRepository productRepository,
            DefaultProductCatalog defaultProductCatalog) {
        this.productRepository = productRepository;
        this.defaultProductCatalog = defaultProductCatalog;
    }

    @Override
    public void run(String @NonNull ... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(defaultProductCatalog.createProducts());
    }
}
