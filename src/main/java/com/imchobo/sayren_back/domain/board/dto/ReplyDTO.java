package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDTO {
  // 댓글 번호
  private Long id;
  // 소속 게시글 번호
  private Long boardId;
//  // 작성자 회원 번호
//  private Long memberId;  // memberId는 토큰기반 인증으로 dto에서는 제외
  // 부모 댓글 번호(대댓글)
  private Long parentReplyId;
  // 댓글 내용
  private String content;
}
