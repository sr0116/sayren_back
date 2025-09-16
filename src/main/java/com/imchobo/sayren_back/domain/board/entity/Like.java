package com.imchobo.sayren_back.domain.board.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_like")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like extends CreatedEntity {
  // 좋아요 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "like_id")
  private Long id;

  // 좋아요 누른 회원번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 좋아요 한 게시글
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  private Board board;

  // 좋아요 한 댓글 Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn (name = "reply_id")
  private Reply reply;

  // 좋아요 누른 시간
  // (createdEntity 만들어둔거)
}
