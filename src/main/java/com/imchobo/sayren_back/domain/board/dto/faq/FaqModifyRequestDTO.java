package com.imchobo.sayren_back.domain.board.dto.faq;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqModifyRequestDTO {
  private String title;
  private String content;
}
