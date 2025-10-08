package com.imchobo.sayren_back.domain.member.dto.admin;

import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AdminDisconnectProviderDTO {
  @NotNull
  private Long memberId;

  @NotNull
  Provider provider;
}
