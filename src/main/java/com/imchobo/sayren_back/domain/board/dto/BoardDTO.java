package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
  // 게시글 번호
  private Long id;
//  // 작성자 회원 번호
//  private Long memberId;  id대신 nickname 으로?
// memberId는 토큰기반 인증으로 dto에서는 제외
  // 카테고리 번호
  private Long categoryId;
  // 상품 번호
  private Long productId;
  // 제목
  private String title;
  // 본문
  private String content;
  // 비밀글 여부
  private boolean isSecret;
}
