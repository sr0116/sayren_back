package com.imchobo.sayren_back.domain.attach.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachDTO {
//  // 첨부파일 번호
//  private Long id;
//  // 파일명 (실제 저장된 UUID)
//  private String uuid;
//  // 파일 경로 (예: 2025/09/09)
//  private String path;
//  // 썸네일 여부
//  private boolean isThumbnail;
//
//  // 연결된 상품 (nullable)
//  private Long productId;
//  // 연결된 게시글 (nullable)
//  private Long boardId;

  // 첨부파일 번호(식별자)
  private Long id;
  // 최종 접근 파일 URL (백에서 path, uuid, 버킷 조합해서 만듦)
  private String url;
}
