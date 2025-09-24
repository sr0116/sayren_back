package com.imchobo.sayren_back.domain.board.dto.notice;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCreateRequestDTO extends BoardRequestDTO {
  private boolean pinned; // 상단 고정 여부 (선택)
}
