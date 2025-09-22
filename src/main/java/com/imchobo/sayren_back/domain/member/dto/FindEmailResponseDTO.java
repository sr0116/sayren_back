package com.imchobo.sayren_back.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FindEmailResponseDTO {
  private String email;
  private LocalDateTime regDate;
}
