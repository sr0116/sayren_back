package com.imchobo.sayren_back.domain.order.cart.service;

import com.imchobo.sayren_back.domain.order.cart.dto.CartItemAddRequestDTO;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.order.cart.exception.CartAlreadyExistsException;
import com.imchobo.sayren_back.domain.order.cart.exception.CartNotFoundException;
import com.imchobo.sayren_back.domain.order.cart.repository.CartRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.OrderPlan.repository.OrderPlanRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final OrderPlanRepository orderPlanRepository;

  @Override
  public void addItem(CartItemAddRequestDTO requestDTO) {
    var member = SecurityUtil.getMemberEntity();

    // 상품 유효성 체크
    var product = productRepository.findById(requestDTO.getProductId())
      .orElseThrow(() -> new CartNotFoundException(requestDTO.getProductId()));

    // 요금제 조회 또는 자동 생성
    OrderPlan orderPlan;

    try {
      // Enum 변환 (대소문자 안전하게)
      OrderPlanType planType = OrderPlanType.valueOf(requestDTO.getType().toUpperCase());

      // 일반구매일 경우 month는 null로 고정
      Integer monthValue = "PURCHASE".equalsIgnoreCase(requestDTO.getType()) ? null : requestDTO.getMonth();

      orderPlan = orderPlanRepository
        .findByTypeAndMonth(planType, monthValue)
        .orElseGet(() -> {
          System.out.printf("📦 새 요금제 생성됨 → type=%s, month=%s%n", planType, monthValue);
          return orderPlanRepository.save(
            OrderPlan.builder()
              .type(planType)
              .month(monthValue)
              .build()
          );
        });
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("잘못된 요금제 타입입니다. type 값은 PURCHASE 또는 RENTAL 이어야 합니다.");
    }

    // 중복 체크 (동일 상품 방지)
    boolean exists = cartRepository.existsByMemberAndProduct(member, product);
    if (exists) {
      throw new CartAlreadyExistsException(requestDTO.getProductId());
    }

    // 장바구니 저장
    cartRepository.save(
      CartItem.builder()
        .member(member)
        .product(product)
        .orderPlan(orderPlan)
        .build()
    );

    System.out.printf("🛒 장바구니 추가 완료 → member=%s, product=%s, planId=%d%n",
      member.getId(), product.getId(), orderPlan.getId());
  }

  // 특정 회원의 장바구니 조회
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