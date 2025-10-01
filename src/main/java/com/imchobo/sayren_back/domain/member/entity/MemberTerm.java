package com.imchobo.sayren_back.domain.member.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.term.entity.Term;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_member_term")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member"})
public class MemberTerm extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_term_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "term_id", nullable = false)
  private Term term;

  @Column(nullable = false)
  private Boolean agreed = true;

  // 약관 버전 (동의 당시 기준)
  @Column(nullable = false, length = 20)
  private String version;
}
