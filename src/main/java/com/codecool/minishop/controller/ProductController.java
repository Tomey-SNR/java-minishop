package com.codecool.minishop.controller;

import com.codecool.minishop.model.Product;
import com.codecool.minishop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        List<Product> products;

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            model.addAttribute(
                    "priceError",
                    "Minimum price cannot be greater than maximum price."
            );

            products = productService.getAllProducts();

        } else if (search != null && !search.isBlank()
                && minPrice != null && maxPrice != null) {

            products = productService.searchProductsByNameAndPrice(
                    search, minPrice, maxPrice
            );

        } else if (search != null && !search.isBlank()) {
            products = productService.searchProducts(search);

        } else if (minPrice != null && maxPrice != null) {
            products = productService.filterProductsByPrice(minPrice, maxPrice);

        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "products/list";
    }

    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found with ID: " + id)
                );

        model.addAttribute("product", product);
        return "products/details";
    }

    @GetMapping("/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "products/form";
        }

        productService.createProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found with ID: " + id)
                );

        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "products/form";
        }

        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
