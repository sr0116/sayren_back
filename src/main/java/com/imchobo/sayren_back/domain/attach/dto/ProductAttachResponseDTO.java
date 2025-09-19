package com.imchobo.sayren_back.domain.attach.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttachResponseDTO {
  // 첨부파일 번호
  private Long attachId;
  // S3 URL (백에서 path, uuid, 버킷 조합)
  private String url;
}
