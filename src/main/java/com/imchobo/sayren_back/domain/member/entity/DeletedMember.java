package com.imchobo.sayren_back.domain.member.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_deleted_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletedMember extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deleted_member_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 암호화된 이메일
  @Column(nullable = false, length = 255)
  private String email;
}
