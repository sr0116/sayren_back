package com.imchobo.sayren_back.domain.board.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_board")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {
  // 상품 번호(pk)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_id")
  private Long id;

  // 작성자 회원 번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 카테고리 번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  // 상품 번호
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product; // 객체 매핑

  // 게시글 제목
  @Column(nullable = false)  // length 제한? length = 200
  private String title;

  // 게시글 본문
  @Column(nullable = false)  // length = 2000
  private String content;

  // 비밀글 여부
  @Column(nullable = false)
  private boolean isSecret = false;

  // 게시글 상태
  @Column(nullable = false)
  private String status = "ACTIVE";
}
