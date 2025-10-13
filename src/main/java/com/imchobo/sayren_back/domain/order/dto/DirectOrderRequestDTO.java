package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectOrderRequestDTO {

  // 기존 배송 정보
  private String receiverName;
  private String receiverTel;
  private String zipcode;
  private String detail;
  private String memo;

  // 바로구매용 추가 필드
  private Long productId;
  private Long planId;
}
