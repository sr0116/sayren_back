package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.dto.DirectOrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {

  OrderResponseDTO createOrder(OrderRequestDTO dto);

  OrderResponseDTO createOrderFromCart(Long addressId);

  OrderResponseDTO createDirectOrder(DirectOrderRequestDTO dto);

  OrderResponseDTO getOrderById(Long orderId);

  List<OrderResponseDTO> getOrdersByMemberId();

  OrderResponseDTO markAsPaid(Long orderId);

  OrderResponseDTO cancel(Long orderId, String reason);
}
