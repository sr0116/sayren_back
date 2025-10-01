package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.product.dto.PurchaseProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.service.PurchaseProductService;
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
        private final PurchaseProductService productService;

        @GetMapping
        public List<PurchaseProductListResponseDTO> list() {
            return productService.getAllProducts();
        }

        @GetMapping("/{id}")
        public PurchaseProductDetailsResponseDTO getOne(@PathVariable Long id) {
            return productService.getProductById(id);
        }
}
