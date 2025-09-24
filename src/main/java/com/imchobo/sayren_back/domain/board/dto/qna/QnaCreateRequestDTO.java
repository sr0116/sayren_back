package com.imchobo.sayren_back.domain.board.dto.qna;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaCreateRequestDTO extends BoardRequestDTO {
  private boolean isSecret;
}
