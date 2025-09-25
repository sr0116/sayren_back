package com.imchobo.sayren_back.domain.board.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDTO {
  // 좋아요 누른 게시글 Id
  private Long boardId;
  // 좋아요 누른 댓글 Id
  private Long replyId;

  @AssertTrue(message = "게시글 ID나 댓글 ID 중 하나는 반드시 입력해야 합니다.")
  public boolean isValidTarget() {
    return (boardId != null && replyId == null) || (boardId == null && replyId != null);
  }
}
