package com.imchobo.sayren_back.domain.board.dto.faq;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqCreateRequestDTO extends BoardRequestDTO {
  private String answer; // FAQ 답변
}
