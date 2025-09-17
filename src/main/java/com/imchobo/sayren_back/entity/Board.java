package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;

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
  private Long id; // 제약조건: PK / auto_increment

  // 작성자 회원 번호
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: Not null / FK (member.id)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 카테고리 번호
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: Not null / FK (category.id)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  // 상품 번호
  @ManyToOne(fetch = FetchType.LAZY) // 제약조건: FK (product.id)
  @JoinColumn(name = "product_id")
  private Product product; // 객체 매핑

  // 게시글 제목
  @Column(nullable = false)  // 제약조건: Not null
  private String title;

  // 게시글 본문
  @Column(nullable = false) // 제약조건: Not null
  private String content;

  // 비밀글 여부
  @Column(nullable = false) // 제약조건: Not null / default=false
  private boolean isSecret = false;

  // 게시글 상태
  @Column(nullable = false) // 제약조건: Not null /  default='ACTIVE'
  private String status = "ACTIVE";
}
