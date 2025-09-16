package com.imchobo.sayren_back.domain.subscribe.subscribe_cancel.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeCancelRequestDTO {
  // 구독 취소 요청
  private Long subscribeId;
  private String reason;
}
