package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryResponseDTO {
  private Long id;
  private Long orderId;
  private String address;
  private String status;  // PENDING, SHIPPING, DELIVERED, RETURNED
}
