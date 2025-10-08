package com.imchobo.sayren_back.domain.member.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AdminSelectMemberIdDTO {
  @NotNull
  private Long memberId;
}
