package com.imchobo.sayren_back.domain.order.cart.repository;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import com.imchobo.sayren_back.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {

  // 회원별 장바구니 전체 조회 (로그인한 회원의 장바구니 목록 확인)
  List<CartItem> findByMemberId(Long memberId);

  // 특정 회원 + 특정 상품 + 특정 요금제 조합으로 이미 담긴 카트 아이템 찾기
  //  상품만 체크하면 안 됨같은 상품이라도 요금제(구매/렌탈) 종류가 다르면 별개 아이템으로 취급해야 하니까
  CartItem findByMemberIdAndProductIdAndOrderPlanId(Long memberId, Long productId, Long orderPlanId);

  
  // 회원 장바구니 전체 비우기 (주문 완료 후 카트 비움)
  void deleteByMemberId(Long memberId);

  boolean existsByMemberAndProduct(Member member, Product product);
}
