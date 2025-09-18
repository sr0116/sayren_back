package com.imchobo.sayren_back.domain.attach.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachResponseDTO {
  // 첨부파일 번호
  private Long AttachId;
  // 최종 URL (백에서 path, uuid, 버킷 조합)
  private String url;
}
