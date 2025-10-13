package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductModifyRequestDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductPendingDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

    @RestController
    @RequestMapping("/api/user/product")
    @RequiredArgsConstructor
    public class ProductController {
        private final ProductService productService;

        @GetMapping
        public ResponseEntity<List<ProductListResponseDTO>> getAllProducts(
                @RequestParam(required = false) String type,
                @RequestParam(required = false) String category) {

            return ResponseEntity.ok(productService.getAllProducts());
        }

        // 사용자용 단일 상품 상세 조회
        @GetMapping("/{id}")
        public ResponseEntity<ProductDetailsResponseDTO> getProduct(@PathVariable Long id) {
            ProductDetailsResponseDTO dto = productService.getProductById(id);

            // 승인되지 않은 상품은 사용자 접근 불가
            if (!dto.getIsUse()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "승인되지 않은 상품입니다.");
            }

            return ResponseEntity.ok(dto);
        }

        // 큐레이션
        @GetMapping("/filter")
        public ResponseEntity<Page<ProductListResponseDTO>> getFilteredProducts(
                ProductListResponseDTO filter,
                @PageableDefault(size = 12) Pageable pageable) {

            Page<ProductListResponseDTO> result = productService.getFilteredProducts(filter, pageable);
            return ResponseEntity.ok(result);
        }

}
