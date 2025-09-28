package com.imchobo.sayren_back.domain.order.OrderPlan.dto;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import lombok.*;

//  요금제 정보를 조회할 때 클라이언트에게 응답으로 전달하는 DTO
//    Entity → ResponseDTO 로 변환해서 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlanResponseDTO {

  // 요금제 ID (PK)
  private Long planId;

  // 요금제 타입 (예: PURCHASE, RENTAL)
  private OrderPlanType type;

  // 기간(개월 수)
  private Integer month;
}
