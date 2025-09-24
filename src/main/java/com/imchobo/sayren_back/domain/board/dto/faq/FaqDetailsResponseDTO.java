package com.imchobo.sayren_back.domain.board.dto.faq;

import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDetailsResponseDTO extends BoardResponseDTO {
  private String answer;
}
