package com.imchobo.sayren_back.domain.member.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_member_login_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginHistory extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "login_history_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false, length = 50)
  private String ip;

  @Column(nullable = false, length = 255)
  private String device;
}
