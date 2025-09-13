package com.imchobo.sayren_back.domain.member.entity;

import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.domain.member.en.TokenStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_member_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberToken extends TimeRangeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_token_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false, unique = true, length = 500)
  private String token;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TokenStatus status = TokenStatus.ACTIVE;
}
