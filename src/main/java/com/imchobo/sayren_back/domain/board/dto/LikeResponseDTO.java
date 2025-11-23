package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {
  // 좋아요 id번호
  private Long id;
  // 좋아요 개수
  private int likeCount;

}
