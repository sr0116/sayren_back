package com.imchobo.sayren_back.domain.product.controller;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.board.service.BoardService;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.*;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final BoardService boardService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    // 상품 등록
//    @PostMapping
//    public ResponseEntity<?> registerProduct(@Valid @RequestBody ProductCreateRequestDTO dto) {
//        return ResponseEntity.ok(productService.registerProduct(dto));
//    }

    // 상품 게시글 등록용 카테고리 목록 조회
    @GetMapping("/category")
    public ResponseEntity<?> getProductCategories() {
        return ResponseEntity.ok(productService.getProductCategories());
    }


    // 게시글 등록
    @PostMapping("/register")
    public ResponseEntity<?> registerProductBoard(@RequestBody ProductCreateRequestDTO dto) {
        boardService.registerProductBoard(dto);
        return ResponseEntity.ok("상품이 게시글로 등록되었습니다.");
    }

//    // 게시글 삭제
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteProductBoard(@PathVariable Long productId) {
//        boardService.deleteProductBoard(productId); // 게시글/상품 상태 변경
//        return ResponseEntity.ok("상품이 삭제되었습니다.");
//    }

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

//    // 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> modifyProduct(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductModifyRequestDTO dto
//    ) {
//        productService.modifyProduct(id, dto);
//        return ResponseEntity.noContent().build();
//    }

}
