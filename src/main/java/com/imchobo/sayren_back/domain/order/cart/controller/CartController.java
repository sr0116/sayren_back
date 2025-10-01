package com.imchobo.sayren_back.domain.order.cart.controller;

import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//  CartController
// - 장바구니 관련 API 컨트롤러
//   add, get, remove, clear 기능 제공
@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  // 장바구니 담기
  @PostMapping
  public ResponseEntity<CartItem> addItem(
    @RequestBody CartRequest request,
    Authentication authentication) {
    // 토큰에서 memberId 추출
    Long memberId = Long.parseLong(authentication.getName());

    CartItem item = cartService.addItem(
      memberId,
      request.getProductId(),
      request.getPlanId(),
      request.getQuantity()
    );
    return ResponseEntity.ok(item);
  }

  // 회원 장바구니 조회
  @GetMapping
  public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
    Long memberId = Long.parseLong(authentication.getName());
    return ResponseEntity.ok(cartService.getCartItems(memberId));
  }

  // 장바구니 단일 아이템 삭제
  @DeleteMapping("/item/{cartItemId}")
  public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
    cartService.removeItem(cartItemId);
    return ResponseEntity.noContent().build();
  }

  // 회원 장바구니 전체 비우기
  @DeleteMapping("/clear")
  public ResponseEntity<Void> clearCart(Authentication authentication) {
    Long memberId = Long.parseLong(authentication.getName());
    cartService.clearCart(memberId);
    return ResponseEntity.noContent().build();
  }

  // DTO 내부 클래스 (요청 Body용)
  @lombok.Data
  static class CartRequest {
    private Long productId;
    private Long planId;
    private int quantity;
  }
}