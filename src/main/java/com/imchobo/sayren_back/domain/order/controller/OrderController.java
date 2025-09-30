package com.imchobo.sayren_back.domain.order.controller;

import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // 주문 생성
  @PostMapping("/create")
  public ResponseEntity<OrderResponseDTO> createOrder(
    @RequestParam Long memberId,
    @RequestParam Long addressId) {
    return ResponseEntity.ok(orderService.createOrderFromCart(memberId, addressId));
  }

  //  단일 주문 조회
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  //  회원별 주문 목록 조회
  @GetMapping("/member/{memberId}")
  public ResponseEntity<List<OrderResponseDTO>> getOrdersByMember(@PathVariable Long memberId) {
    return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
  }

  //  결제 성공 → 주문 상태 PAID
  @PostMapping("/{id}/paid")
  public ResponseEntity<OrderResponseDTO> markAsPaid(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.markAsPaid(id));
  }

  //  결제 실패/취소 → 주문 상태 CANCELED
  @PostMapping("/{id}/cancel")
  public ResponseEntity<OrderResponseDTO> cancelOrder(
    @PathVariable Long id,
    @RequestParam(required = false) String reason) {
    return ResponseEntity.ok(orderService.cancel(id, reason));
  }
}
