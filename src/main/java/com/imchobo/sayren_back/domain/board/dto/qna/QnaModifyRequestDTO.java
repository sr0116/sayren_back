package com.imchobo.sayren_back.domain.board.dto.qna;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaModifyRequestDTO extends BoardRequestDTO {
  private Long boardId;   // 수정할 게시글 번호
  private boolean isSecret;
}
