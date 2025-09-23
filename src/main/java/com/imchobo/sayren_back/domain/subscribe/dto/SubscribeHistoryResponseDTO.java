package com.imchobo.sayren_back.domain.subscribe.dto;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
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

  private ReasonCode reasonCode;
  private ActorType changedBy;
  private LocalDateTime regDate;
}
