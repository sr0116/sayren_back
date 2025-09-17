package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_member_provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberProvider extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_provider_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private Provider provider;

  @Column(nullable = false, unique = true, length = 255)
  private String providerUid;

  @Column(nullable = false, unique = true, length = 255)
  private String email;
}
