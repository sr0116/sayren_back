package com.imchobo.sayren_back.domain.order.dto;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponseDTO {

  private Long id;             // 히스토리 PK
  private OrderStatus status;  // 변경된 상태
  private String address;      // 변경 시점 배송지
  private ActorType changedBy; // 변경 주체
  private LocalDateTime regDate; // 변경 시각
}