package com.imchobo.sayren_back.domain.member.dto.admin;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AdminChangeMemberStatusDTO {
  @NotNull
  private Long memberId;

  @NotNull
  private MemberStatus status;
}
