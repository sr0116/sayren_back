package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeliveryResponseDTO {
  private Long deliveryId;
  private Long memberId;
  private Long addressId;
  private String type;     // enum → String
  private String status;   // enum → String
}
