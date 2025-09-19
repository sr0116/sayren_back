package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.entity.CartItem;

import java.util.List;

public interface CartService {
  // 장바구니 담기
  CartItem addItem(Long memberId, Long productId, Long planId, int quantity);

  // 회원 장바구니 조회
  List<CartItem> getCartItems(Long memberId);

  // 장바구니 단일 아이템 삭제
  void removeItem(Long cartItemId);

  // 회원 장바구니 전체 비우기
  void clearCart(Long memberId);
}
