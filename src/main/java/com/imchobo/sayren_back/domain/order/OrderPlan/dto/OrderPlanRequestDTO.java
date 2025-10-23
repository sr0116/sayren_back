package com.imchobo.sayren_back.domain.order.OrderPlan.dto;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlanRequestDTO {

  // 요금제 타입
  private OrderPlanType type;

  // 기간(개월 수)
  private Integer month;
}
