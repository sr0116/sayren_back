package com.imchobo.sayren_back.domain.order.service;
import com.imchobo.sayren_back.domain.order.dto.OrderDTO;
import java.util.List;

public interface OrderService {
  // 주문 등록
  OrderDTO createOrderFromCart(Long memberId, Long addressId);

  // 단건 조회 (회원 + 주소 포함)
  OrderDTO getOrderById(Long orderId);

  // 회원별 주문 조회
  List<OrderDTO> getOrdersByMemberId(Long memberId);
}
