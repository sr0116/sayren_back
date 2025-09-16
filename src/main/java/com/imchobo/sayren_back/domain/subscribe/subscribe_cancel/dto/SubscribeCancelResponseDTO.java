package com.imchobo.sayren_back.domain.subscribe.subscribe_cancel.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeCancelResponseDTO {
  // 구독 취소 응단
  private Long cancelId;
  private Long subscribeId;
//  memberId 는 나중에 시큐리티에서 받아올 예정(나중에 주석 삭제)
  private String reason;
  private String status;
  private LocalDateTime regdate; // 지금 extends할때 time엔티티 필드명이랑 다른 엔티티 필드명 다름 (확인 필요)
  private LocalDateTime voiddate;
  private String processedBy;
}
