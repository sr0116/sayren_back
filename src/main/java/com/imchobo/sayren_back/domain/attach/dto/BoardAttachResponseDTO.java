package com.imchobo.sayren_back.domain.attach.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardAttachResponseDTO {
  // 첨부파일 번호
  private Long attachId;
  // S3 URL
  private String url;

  private String fileName; // 원본 파일명
  private Long boardId;    // 어떤 게시판 글 소속인지
  private boolean thumbnail;
}
