package com.imchobo.sayren_back.domain.board.dto.faq;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDetailsResponseDTO {
  private Long boardId;
  private String title;
  private String content;

  private String categoryName;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
