package com.imchobo.sayren_back.domain.attach.dto;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardAttachRequestDTO {
  // 원본 파일명
  private String fileName;
  // MIME 타입
  private String contentType;
  // 파일 크기
  private Long size;
  // 게시판 글 번호 (FK)
  private Long boardId;
  // 대표이미지 여부
  private boolean isThumbnail;

}
