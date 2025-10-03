package com.imchobo.sayren_back.domain.member.dto.admin;

import com.imchobo.sayren_back.domain.member.en.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode
public class MemberDetailResponseDTO {
  private String email;
  private String name;
  private String tel;
  private String status;
  private Set<Role> roles;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
