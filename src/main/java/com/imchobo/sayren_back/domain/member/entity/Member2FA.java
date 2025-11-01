package com.imchobo.sayren_back.domain.member.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_member_2fa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member"})
public class Member2FA extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_2fa_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false, unique = true)
  private Member member;

  @Column(nullable = false, unique = true, length = 255)
  private String secret;
}
