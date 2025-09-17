package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column
  private String password;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 20, unique = true)
  private String tel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MemberStatus status = MemberStatus.READY;

  @Column(nullable = false)
  private Boolean emailVerified = false;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
          name = "tbl_member_role",
          joinColumns = @JoinColumn(name = "member_id")
  )
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 50)
  @Builder.Default
  private Set<Role> roles = new HashSet<>(Set.of(Role.USER));
}
