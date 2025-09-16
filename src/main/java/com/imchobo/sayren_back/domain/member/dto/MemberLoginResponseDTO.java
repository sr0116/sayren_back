package com.imchobo.sayren_back.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponseDTO {
  private String accessToken;
  private String message;
}