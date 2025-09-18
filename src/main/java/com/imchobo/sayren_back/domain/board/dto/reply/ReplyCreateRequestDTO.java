package com.imchobo.sayren_back.domain.board.dto.reply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequestDTO {
  // 속해있는 게시글 번호
  @NotNull(message = "게시글 번호는 필수입니다.")
  private Long boardId;
  // 부모 댓글 번호
  private Long parentReplyId;
  // 댓글 내용
  @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
  private String content;
}
