package com.imchobo.sayren_back.domain.subscribe.subscribe_history.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscribeHistoryResponseDTO {
// 응답
  private Long historyId;
  private Long subscribeId;
  private String status; // 변경된 상태
  private String reason;
  private String changedBy;
  private LocalDateTime regDate;
}
