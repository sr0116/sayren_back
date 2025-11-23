package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.dto.DirectOrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {
//  일반 주문 생성 (장바구니 아닌 직접 주문)
  OrderResponseDTO createOrder(OrderRequestDTO dto);
//  장바구니에 담긴 상품들을 한 번에 주문으로 생성
  OrderResponseDTO createOrderFromCart(Long addressId);
//  상품 상세 페이지에서 바로 바로구매로 주문 생성
  OrderResponseDTO createDirectOrder(DirectOrderRequestDTO dto);
//  주문 ID로 특정 주문의 상세정보 조회
  OrderResponseDTO getOrderById(Long orderId);
//  현재 로그인한 회원의 전체 주문 목록 조회
  List<OrderResponseDTO> getOrdersByMemberId();
//  결제 완료 후 주문 상태를 결제됨(PAID) 으로 변경
  OrderResponseDTO markAsPaid(Long orderId);
//  주문 취소 처리 및 취소 사유 저장
  OrderResponseDTO cancel(Long orderId, String reason);
}
