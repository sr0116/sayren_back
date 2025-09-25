package com.imchobo.sayren_back.domain.board.dto.notice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCreateRequestDTO  {
  private String title;
  private String content;
}
