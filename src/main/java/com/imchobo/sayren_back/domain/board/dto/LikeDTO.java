package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDTO {
  // 좋아요 번호
  private Long id;
//  // 좋아요 누른 회원ID
//  private Long memberId;  // memberId는 토큰기반 인증으로 dto에서는 제외
  // 좋아요 한 게시글ID (게시글에 좋아요)
  private Long boardId;
  // 좋아요 한 댓글ID (댓글에 좋아요)
  private Long replyId;
}
