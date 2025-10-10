package com.imchobo.sayren_back.domain.order.controller;

import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // 장바구니 → 주문 생성
  @PostMapping("/create")
  public ResponseEntity<OrderResponseDTO> createOrder(
    @RequestBody OrderRequestDTO dto,
    Authentication authentication) {
    Long memberId = Long.valueOf(authentication.getName());
    return ResponseEntity.ok(orderService.createOrder(memberId, dto)); // 변경됨
  }

  // 내 주문 전체 조회
  @GetMapping("/my")
  public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
    Long memberId = Long.valueOf(authentication.getName());
    return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
  }

  // 단일 주문 조회
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  // 결제 완료 → 상태 PAID
  @PostMapping("/{id}/paid")
  public ResponseEntity<OrderResponseDTO> markAsPaid(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.markAsPaid(id));
  }

  // 결제 실패/취소 → 상태 CANCELED
  @PostMapping("/{id}/cancel")
  public ResponseEntity<OrderResponseDTO> cancelOrder(
    @PathVariable Long id,
    @RequestParam(required = false) String reason) {
    return ResponseEntity.ok(orderService.cancel(id, reason));
  }
}