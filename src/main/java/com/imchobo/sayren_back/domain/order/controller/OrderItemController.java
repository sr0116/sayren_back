package com.imchobo.sayren_back.domain.order.controller;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/order-items")
@RequiredArgsConstructor
public class OrderItemController {

  private final OrderItemService orderItemService;

  // 단일 주문 아이템 조회
  @GetMapping("/{id}")
  public ResponseEntity<OrderItemResponseDTO> getOrderItem(@PathVariable Long id) {
    return ResponseEntity.ok(orderItemService.getOrderItem(id));
  }
}