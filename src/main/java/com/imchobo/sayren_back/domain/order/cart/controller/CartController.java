package com.imchobo.sayren_back.domain.order.cart.controller;

import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.service.CartService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
@Log4j2
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
        return ResponseEntity.ok(cartService.getCartItems());
    }


      //장바구니 단일 아이템 삭제

    @DeleteMapping("/delete-item/{cartItemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.ok().build();
    }


     // 회원 장바구니 전체 비우기

    @DeleteMapping("/clear-item")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(Map.of("message", "success"));
    }
}
