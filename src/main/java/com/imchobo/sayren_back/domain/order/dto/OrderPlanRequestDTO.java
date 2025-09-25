package com.imchobo.sayren_back.domain.order.dto;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import lombok.*;

// 요금제 생성/수정 요청을 받을 때 사용하는 DTO
//    클라이언트가 "type, month" 값을 보내면 이 DTO로 받음
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlanRequestDTO {

  // 요금제 타입 (예: PURCHASE, RENTAL)
  private OrderPlanType type;

  // 기간(개월 수)
  private Integer month;
}
