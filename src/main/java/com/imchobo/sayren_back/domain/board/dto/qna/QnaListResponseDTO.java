package com.imchobo.sayren_back.domain.board.dto.qna;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaListResponseDTO {
  private Long boardId;
  private String title;
  private boolean isSecret;
  private LocalDateTime regDate;
}
