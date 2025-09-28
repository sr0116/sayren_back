package com.imchobo.sayren_back.domain.board.dto.notice;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailsResponseDTO  {
  private Long boardId;
  private String title;
  private String content;

  private String categoryName;
  private LocalDateTime regDate;
  private LocalDateTime modDate;

  private BoardAttachResponseDTO thumbnail;
  private List<BoardAttachResponseDTO> attachList;
}
