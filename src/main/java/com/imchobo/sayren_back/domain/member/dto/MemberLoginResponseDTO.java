package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class MemberLoginResponseDTO {
  @NotNull
  private Long id;

  @NotBlank
  private String name;

  @NotNull
  private Set<Role> roles;

  @NotNull
  private MemberStatus status;

  @NotBlank
  private boolean emailVerified;
}
