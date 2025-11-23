package com.imchobo.sayren_back.domain.attach.dto;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardAttachRequestDTO {
  private String uuid;

  private String path;

}
