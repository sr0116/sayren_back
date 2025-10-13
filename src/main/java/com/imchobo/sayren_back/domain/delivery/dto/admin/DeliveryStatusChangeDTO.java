package com.imchobo.sayren_back.domain.delivery.dto.admin;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryStatusChangeDTO {
  private Long deliveryId;
  private DeliveryStatus status;
}
