package com.imchobo.sayren_back.domain.term.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TermResponseDTO {
  private String content;
  private LocalDateTime regDate;
}
