package com.imchobo.sayren_back.domain.order.cart.service;

import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;

import java.util.List;

public interface CartService {
  // 장바구니 담기
  void addItem(CartItemAddRequestDTO requestDTO);

  // 회원 장바구니 조회 (DTO 반환)
  List<CartItemResponseDTO> getCartItems();

  // 장바구니 단일 아이템 삭제
  void removeItem(Long memberId, Long cartItemId);


  // 회원 장바구니 전체 비우기
  void clearCart();
}