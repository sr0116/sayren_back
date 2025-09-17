package com.imchobo.sayren_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_attach")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attach {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "attach_id") // 제약조건: PK / auto_increment
  private Long id;

  // 파일명 (ex: ffbca870-ba14-4d29-9b26-1e01271abaa6.webp)
  @Column(nullable = false, unique = true)  // 제약조건: Not null / unique
  private String uuid;

  // 경로 (ex: 2025/09/09)
  @Column(nullable = false) // 제약조건: Not null
  private String path;

  // true=썸네일, false=상세이미지
  @Column(nullable = false) // 제약조건: Not null / default=false
  private boolean isThumbnail = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id") // 제약조건: FK
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id") // 제약조건: FK
  private Board board;
}

