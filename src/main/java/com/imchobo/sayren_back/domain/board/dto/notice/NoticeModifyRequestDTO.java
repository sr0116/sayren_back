package com.imchobo.sayren_back.domain.board.dto.notice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeModifyRequestDTO  {
  private String title;
  private String content;
}
