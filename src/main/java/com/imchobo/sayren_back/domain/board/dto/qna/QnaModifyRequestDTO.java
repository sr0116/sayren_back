package com.imchobo.sayren_back.domain.board.dto.qna;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaModifyRequestDTO {
  private String title;
  private String content;
  private boolean isSecret;
}
