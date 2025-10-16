package com.imchobo.sayren_back.domain.order.controller;

import com.imchobo.sayren_back.domain.order.dto.DirectOrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.service.OrderService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  //  장바구니 및 단일 주문 생성
  @PostMapping("/create")
  public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequestDTO dto) {
    log.info("[주문 생성 요청] memberId={}", SecurityUtil.getMemberAuthDTO().getId());
    OrderResponseDTO response = orderService.createOrder(dto);

    // 첫 번째 orderItemId 추출
    Long orderItemId = null;
    if (response.getOrderItems() != null && !response.getOrderItems().isEmpty()) {
      orderItemId = response.getOrderItems().get(0).getOrderItemId();
    }

    return ResponseEntity.ok(Map.of(
            "message", "success",
            "orderId", response.getOrderId(),
            "orderItemId", orderItemId, //  프론트에서 바로 접근 가능
            "data", response
    ));
  }

  //바로구매 주문 생성 (장바구니 생략)
  @PostMapping("/direct-create")
  public ResponseEntity<?> createDirectOrder(@RequestBody @Valid DirectOrderRequestDTO dto) {
    log.info("[바로구매 주문 요청] memberId={}, productId={}, planId={}",
      SecurityUtil.getMemberAuthDTO().getId(),
      dto.getProductId(), dto.getPlanId());

    OrderResponseDTO response = orderService.createDirectOrder(dto);
    return ResponseEntity.ok(Map.of(
      "message", "success",
      "orderId", response.getOrderId(),
      "data", response
    ));
  }

  //내 주문 전체 조회
  @GetMapping("/my")
  public ResponseEntity<?> getMyOrders() {
    log.info("[내 주문 전체 조회] memberId={}", SecurityUtil.getMemberAuthDTO().getId());
    List<OrderResponseDTO> response = orderService.getOrdersByMemberId();
    return ResponseEntity.ok(Map.of("message", "success", "data", response));
  }

  //단일 주문 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> getOrder(@PathVariable Long id) {
    log.info("[단일 주문 조회] orderId={}", id);
    return ResponseEntity.ok(Map.of(
      "message", "success",
      "data", orderService.getOrderById(id)
    ));
  }

  //결제 완료 → 상태 PAID
  @PostMapping("/{id}/paid")
  public ResponseEntity<?> markAsPaid(@PathVariable Long id) {
    log.info("[결제 완료 처리] orderId={}", id);
    return ResponseEntity.ok(Map.of(
      "message", "success",
      "data", orderService.markAsPaid(id)
    ));
  }

  //결제 실패/취소 → 상태 CANCELED
  @PostMapping("/{id}/cancel")
  public ResponseEntity<?> cancelOrder(@PathVariable Long id,
                                       @RequestParam(required = false) String reason) {
    log.info("[결제 취소 처리] orderId={}, reason={}", id, reason);
    return ResponseEntity.ok(Map.of(
      "message", "success",
      "data", orderService.cancel(id, reason)
    ));
  }
}
