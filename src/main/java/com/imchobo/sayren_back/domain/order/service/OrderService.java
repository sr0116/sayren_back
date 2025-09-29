package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

  // 주문 등록 (장바구니 → 주문 생성)
  OrderResponseDTO createOrderFromCart(Long memberId, Long addressId);

  // 단건 조회 (회원 + 주소 포함)
  OrderResponseDTO getOrderById(Long orderId);

  // 회원별 주문 조회
  List<OrderResponseDTO> getOrdersByMemberId(Long memberId);

  //  결제 성공 → 주문 상태 PAID
  OrderResponseDTO markAsPaid(Long orderId);

  //  결제 실패/취소 → 주문 상태 CANCELED
  OrderResponseDTO cancel(Long orderId, String reason);
}