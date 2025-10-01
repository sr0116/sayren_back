package com.imchobo.sayren_back.domain.order.cart.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.cart.mapper.CartItemMapper;
import com.imchobo.sayren_back.domain.order.cart.repository.CartRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.order.OrderPlan.repository.OrderPlanRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;

    @Override
    public void addItem(CartItemAddRequestDTO requestDTO) {
      cartRepository.save(CartItem.builder()
        .member(SecurityUtil.getMemberEntity())
        .product(Product.builder().id(requestDTO.getProductId()).build())
        .orderPlan(OrderPlan.builder().id(requestDTO.getPlanId()).build())
        .build());
    }

    /**
     * 특정 회원의 장바구니 아이템 전체 조회 (ResponseDTO 변환)
     */
    @Override
    public List<CartItemResponseDTO> getCartItems(Long memberId) {
      return cartRepository.findByMemberId(memberId)
        .stream()
        .map(cartItem -> CartItemResponseDTO.builder()
          .cartItemId(cartItem.getId())
          .productId(cartItem.getProduct().getId())
          .productName(cartItem.getProduct().getName())
          .planId(cartItem.getOrderPlan() != null ? cartItem.getOrderPlan().getId() : null)
          .planType(cartItem.getOrderPlan() != null ? cartItem.getOrderPlan().getType().name() : null)
          .price(cartItem.getProduct().getPrice())
          .build())
        .toList();
    }

    @Override
    public void removeItem(Long cartItemId) {
      cartRepository.deleteById(cartItemId);
    }

    @Override
    public void clearCart(Long memberId) {
      cartRepository.deleteByMemberId(memberId);
    }
  }