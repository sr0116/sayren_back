package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter @Setter
public class DeliveryResponseDTO {
  private Long deliveryId;
  private Long memberId;
  private Long addressId;
  private String type;     // enum → String
  private String status;   // enum → String
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
