package com.imchobo.sayren_back.domain.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyListResponseDTO {
  // 댓글 번호
  private Long id;
  // 속해있는 게시글 번호
  private Long boardId;
  // 부모 댓글 번호
  private Long parentReplyId;
  // 댓글 내용
  private String content;
  // 작성일
  private LocalDateTime regDate;
  // 수정일
  private LocalDateTime modDate;

//  // (표시용) 작성자 이름
//  private String writerName;


}
