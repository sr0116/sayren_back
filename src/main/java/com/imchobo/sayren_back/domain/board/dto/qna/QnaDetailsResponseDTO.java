package com.imchobo.sayren_back.domain.board.dto.qna;

import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaDetailsResponseDTO extends BoardResponseDTO {
  private boolean isSecret; // 비밀글 여부
  private boolean isAnswered; // 답변 여부
}
