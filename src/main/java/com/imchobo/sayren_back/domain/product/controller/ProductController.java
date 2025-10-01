package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    @RestController
    @RequestMapping("/api/admin/products")
    @RequiredArgsConstructor
    public class ProductController {
        private final ProductService productService;

        @GetMapping
        public List<ProductListResponseDTO> list() {
            return productService.getAllProducts();
        }

        @GetMapping("/{id}")
        public ProductDetailsResponseDTO getOne(@PathVariable Long id) {
            return productService.getProductById(id);
        }
}
