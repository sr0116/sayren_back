package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class MemberListResponseDTO {
  private Long id;
  private String email;
  private String name;
  private String tel;
  private MemberStatus status;
  private Set<Role> roles;
}
