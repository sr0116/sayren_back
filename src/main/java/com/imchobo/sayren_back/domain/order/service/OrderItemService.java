package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;

public interface OrderItemService {

  // 단건 조회
  OrderItemResponseDTO getOrderItem(Long id);
}
