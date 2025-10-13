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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductListResponseDTO> list(@RequestParam(required = false) String type, String category) {
        return productService.getAllProducts();
    }

    // 승인처리
    @PostMapping("/use/{id}")
    public ResponseEntity<?> useProduct(@PathVariable Long id) {
        productService.useProduct(id);
        return ResponseEntity.ok("상품 승인 완료");
    }

    // 승인 대기목록
    @GetMapping("/pending")
    public ResponseEntity<PageResponseDTO<ProductPendingDTO, Product>> getPendingProducts(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ProductPendingDTO, Product> response = productService.getPendingProducts(pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    // 승인 완료(활성화) 상품 목록
    @GetMapping("/approved")
    public ResponseEntity<PageResponseDTO<ProductPendingDTO, Product>> getApprovedProducts(PageRequestDTO requestDTO) {
        return ResponseEntity.ok(productService.getApprovedProducts(requestDTO));
    }

    // 승인 취소(삭제/ 등록 대기로 전환)
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelApprovedProduct(@PathVariable Long id) {
        productService.cancelUseProduct(id);
        return ResponseEntity.ok("상품이 삭제(비활성화) 되었습니다");
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsResponseDTO> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDetailForAdmin(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductModifyRequestDTO dto
    ) {
        productService.modifyProduct(id, dto);
        return ResponseEntity.noContent().build();
    }

}
