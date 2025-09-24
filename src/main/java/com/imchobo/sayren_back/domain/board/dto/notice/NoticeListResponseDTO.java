package com.imchobo.sayren_back.domain.board.dto.notice;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListResponseDTO {
  private Long boardId;
  private String title;
  private LocalDateTime regDate;
}
