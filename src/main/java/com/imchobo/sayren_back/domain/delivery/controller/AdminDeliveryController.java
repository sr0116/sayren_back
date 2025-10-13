package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.admin.DeliveryStatusChangeDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/deliveries")
@RequiredArgsConstructor
public class AdminDeliveryController {
  private final DeliveryService deliveryService;

  @GetMapping("get-list")
  public ResponseEntity<?> getDeliveryList(PageRequestDTO pageRequestDTO) {
    return ResponseEntity.ok(deliveryService.getAllList(pageRequestDTO));
  }

  @PostMapping("change-status")
  public ResponseEntity<?> changeDeliveryStatus(@RequestBody DeliveryStatusChangeDTO deliveryStatusChangeDTO){
    deliveryService.changeStatus(deliveryStatusChangeDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }
}
