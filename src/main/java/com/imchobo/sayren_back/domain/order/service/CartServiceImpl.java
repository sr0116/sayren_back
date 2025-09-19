package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.entity.CartItem;
import com.imchobo.sayren_back.domain.order.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.repository.CartRepository;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.order.repository.OrderPlanRepository;
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
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final OrderPlanRepository orderPlanRepository;

  /**
   * 장바구니 담기 기능
   * - 회원 ID, 상품 ID, 요금제 ID, 수량을 받아서 장바구니에 저장
   */
  @Override
  public CartItem addItem(Long memberId, Long productId, Long planId, int quantity) {
    // 1. 회원 검증 (DB에서 해당 memberId 찾기, 없으면 예외 발생)
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new EntityNotFoundException("회원 없음: id=" + memberId));

    // 2. 상품 검증 (상품 존재하지 않으면 예외 발생)
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new EntityNotFoundException("상품 없음: id=" + productId));

    // 3. 요금제 검증 (무조건 있어야 함, null 허용 X)
    if (planId == null) {
      throw new IllegalArgumentException("요금제(planId)는 필수입니다.");
    }
    OrderPlan plan = orderPlanRepository.findById(planId)
      .orElseThrow(() -> new EntityNotFoundException("요금제 없음: id=" + planId));

    // 4. 이미 같은 상품 + 같은 요금제를 담은 장바구니 아이템 있는지 확인
    CartItem existingItem = cartRepository.findByMemberIdAndProductIdAndOrderPlanId(memberId, productId, planId);

    if (existingItem != null) {
      // 이미 존재하면 수량만 증가시키고 저장
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
      return cartRepository.save(existingItem);
    }

    // 5. 새로운 장바구니 아이템 생성
    CartItem cartItem = CartItem.builder()
      .member(member)
      .product(product)
      .orderPlan(plan)
      .quantity(quantity)
      .build();

    // 6. DB 저장 후 반환
    return cartRepository.save(cartItem);
  }

  /**
   * 특정 회원의 장바구니 아이템 전체 조회
   */
  @Override
  public List<CartItem> getCartItems(Long memberId) {
    return cartRepository.findByMemberId(memberId);
  }

  /**
   * 장바구니 단일 아이템 삭제
   */
  @Override
  public void removeItem(Long cartItemId) {
    cartRepository.deleteById(cartItemId);
  }

  /**
   * 회원 장바구니 전체 비우기
   */
  @Override
  public void clearCart(Long memberId) {
    cartRepository.deleteByMemberId(memberId);
  }
}
