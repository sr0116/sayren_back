package com.imchobo.sayren_back.security.dto;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberAuthDTO implements UserDetails, OAuth2User {
  private Long id;
  private String email;
  private String password;
  private MemberStatus status;
  private Set<Role> roles;
  private Map<String, Object> attributes;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .toList();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return status.equals(MemberStatus.ACTIVE) || status.equals(MemberStatus.READY);
  }

  @Override // 비밀번호 만료 여부
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override // 계정 잠겨있는지 확인
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override // 계정 만료 여부
  public boolean isAccountNonExpired() {
    return true;
  }

  // Oauth2User
  @Override
  public String getName() {
    return attributes.get("email") != null ? attributes.get("email").toString() : email;
  }
}
