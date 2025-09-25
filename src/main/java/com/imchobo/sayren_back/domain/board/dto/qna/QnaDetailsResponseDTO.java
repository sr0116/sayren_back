package com.imchobo.sayren_back.domain.board.dto.qna;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaDetailsResponseDTO {
  private Long boardId;
  private String title;
  private String content;
  private boolean isSecret;

  private String categoryName;
  private int replyCount;

  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
