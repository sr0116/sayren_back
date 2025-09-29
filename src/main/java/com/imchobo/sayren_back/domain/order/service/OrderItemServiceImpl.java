package com.imchobo.sayren_back.domain.order.service;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

  private final OrderItemRepository orderItemRepository;

  @Override
  @Transactional(readOnly = true)
  public OrderItemResponseDTO getOrderItem(Long id) {
    OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderItem not found: " + id));

    return OrderItemResponseDTO.builder()
            .orderItemId(orderItem.getId())
            .productId(orderItem.getProduct().getId())
            .productName(orderItem.getProduct().getName())
            .priceSnapshot(orderItem.getProductPriceSnapshot())
            .planId(orderItem.getOrderPlan() != null ? orderItem.getOrderPlan().getId() : null)
            .build();
  }
}