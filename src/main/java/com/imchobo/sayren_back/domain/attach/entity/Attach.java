package com.imchobo.sayren_back.domain.attach.entity;

import com.imchobo.sayren_back.domain.product.entity.Product;
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
  @Column(name = "product_attach_id")
  private Long id;

  // 파일명 (ex: ffbca870-ba14-4d29-9b26-1e01271abaa6.webp)
  @Column(nullable = false, unique = true)
  private String uuid;

  // 경로 (ex: 2025/09/09)
  @Column(nullable = false)
  private String path;

  @Column(nullable = false)
  private boolean isThumbnail = false; // true=썸네일, false=상세이미지

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;
}
