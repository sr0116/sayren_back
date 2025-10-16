package com.imchobo.sayren_back.domain.order.OrderPlan.controller;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanRequestDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.service.OrderPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/order-plans")
@RequiredArgsConstructor
public class OrderPlanController {

  private final OrderPlanService orderPlanService;

     //요금제 등록

  @PostMapping
  public ResponseEntity<OrderPlanResponseDTO> create(@RequestBody OrderPlanRequestDTO dto) {
    return ResponseEntity.ok(orderPlanService.create(dto));
  }


   //요금제 수정

  @PutMapping("/{id}")
  public ResponseEntity<OrderPlanResponseDTO> update(
    @PathVariable Long id,
    @RequestBody OrderPlanRequestDTO dto
  ) {
    return ResponseEntity.ok(orderPlanService.update(id, dto));
  }


   //요금제 삭제

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    orderPlanService.delete(id);
    return ResponseEntity.noContent().build();
  }


   //단일 요금제 조회

  @GetMapping("/{id}")
  public ResponseEntity<OrderPlanResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(orderPlanService.getById(id));
  }


    //전체 요금제 목록 조회

  @GetMapping
  public ResponseEntity<List<OrderPlanResponseDTO>> getAll() {
    return ResponseEntity.ok(orderPlanService.getAll());
  }
}
