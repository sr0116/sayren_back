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
  @Column(name = "like_id")  // 제약조건: PK / auto_increment
  private Long id;

  // 좋아요 누른 회원번호
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: Notnull / FK
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 좋아요 한 게시글
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: FK
  @JoinColumn(name = "board_id")
  private Board board;

  // 좋아요 한 댓글 Id
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: FK
  @JoinColumn (name = "reply_id")
  private Reply reply;
}
