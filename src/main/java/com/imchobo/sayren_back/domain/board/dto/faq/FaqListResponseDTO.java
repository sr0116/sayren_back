package com.imchobo.sayren_back.domain.board.dto.faq;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqListResponseDTO {
  private Long boardId;
  private String title;     // 질문
  private LocalDateTime regDate;
}
