package com.imchobo.sayren_back.domain.member.dto.admin;

import com.imchobo.sayren_back.domain.term.en.TermType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class MemberDetailTermResponseDTO {
  private TermType termType;
  private Boolean agreed;
  private String version;
}
