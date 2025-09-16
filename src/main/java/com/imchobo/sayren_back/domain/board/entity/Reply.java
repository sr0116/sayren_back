package com.imchobo.sayren_back.domain.board.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_reply")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reply extends BaseEntity {
  // 댓글 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reply_id")
  private Long id;

  // 소속 게시글 번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  private Board board;

  // 작성자 회원번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 부모 댓글번호(대댓글)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_reply_id")
  private Reply parentReply;

  //댓글 내용
  @Column(nullable = false)
  private String content;

  // 댓글 상태
  @Column(nullable = false)
  @Builder.Default
  private String status = "ACTIVE";
}
