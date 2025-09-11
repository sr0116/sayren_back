package com.imchobo.sayren_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_attach_crawl")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor

public class AttachCrawl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long attachId;

  private String uuid;   // 파일명 (ex: ffbca870-ba14-4d29-9b26-1e01271abaa6.webp)

  private String path;   // 경로 (ex: 2025/09/09)

  private boolean isThumbnail = false; // true=썸네일, false=상세이미지

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductCrawl productCrawl;

}
