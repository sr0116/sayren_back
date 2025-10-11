package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/admin/product")
    @RequiredArgsConstructor
    public class ProductController {
        private final ProductService productService;

        @GetMapping
        public List<ProductListResponseDTO> list(@RequestParam(required = false) String type, String category) {
            return productService.getAllProducts(type, category);
        }

        // 승인처리
        @PostMapping("/use/{id}")
        public ResponseEntity<?> useProduct(@PathVariable Long id) {
            productService.useProduct(id);
            return ResponseEntity.ok("상품 승인 완료");
        }

        // 승인 대기목록
        @GetMapping("/pending")
        public ResponseEntity<?> getPendingProducts() {
            return ResponseEntity.ok(productService.getPendingProducts());
        }

        // 승인 처리된 상품 목록
        @GetMapping("/approved")
        public ResponseEntity<?> getApprovedProducts() {
            return ResponseEntity.ok(productService.getApprovedProducts());
        }

        // 승인 취소(삭제/ 등록 대기로 전환)
        @PostMapping("/cancel/{id}")
        public ResponseEntity<?> cancelApprovedProduct(@PathVariable Long id) {
            productService.cancelUseProduct(id);
            return ResponseEntity.ok("상품이 삭제(비활성화) 되었습니다");
        }
}
