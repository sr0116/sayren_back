package com.imchobo.sayren_back.domain.order.cart.controller;

import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.service.CartService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


     // 장바구니 담기

    @PostMapping("/add-item")
    public ResponseEntity<?> addItem(@RequestBody @Valid CartItemAddRequestDTO cartItemAddRequestDTO) {
        cartService.addItem(cartItemAddRequestDTO);
        return ResponseEntity.ok(Map.of("message", "success"));
    }


      //회원 장바구니 조회

    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getCart() {
        Long memberId = SecurityUtil.getMemberEntity().getId();
        return ResponseEntity.ok(cartService.getCartItems(memberId));
    }


      //장바구니 단일 아이템 삭제

    @DeleteMapping("/delete-item/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.noContent().build();
    }


     // 회원 장바구니 전체 비우기

    @DeleteMapping("/clear-item")
    public ResponseEntity<Void> clearCart() {
        Long memberId = SecurityUtil.getMemberEntity().getId();
        cartService.clearCart(memberId);
        return ResponseEntity.noContent().build();
    }
}
