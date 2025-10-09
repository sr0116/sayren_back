package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/admin/products")
    @RequiredArgsConstructor
    public class ProductController {
        private final ProductService productService;

        @GetMapping
        public List<ProductListResponseDTO> list(@RequestParam(required = false) String type, String category) {
            return productService.getAllProducts(type, category);
        }

        @GetMapping("/{id}")
        public ProductDetailsResponseDTO getOne(@PathVariable Long id) {
            return productService.getProductById(id);
        }

        @PostMapping("/admin/products/use/{id}")
        public ResponseEntity<?> useProduct(@PathVariable Long id) {
            productService.useProduct(id);
            return ResponseEntity.ok("상품 승인 완료");
        }

}
