package com.imchobo.sayren_back.domain.member.dto.admin;

import com.imchobo.sayren_back.domain.member.en.Provider;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class MemberDetailProviderResponseDTO {
  private Provider provider;
  private String email;
  private LocalDateTime regDate;
}
