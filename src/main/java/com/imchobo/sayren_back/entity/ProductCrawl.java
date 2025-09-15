package com.imchobo.sayren_back.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_product_crawl")  // 테이블명만 바꿈
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ProductCrawl {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long productId;

  private String name;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String description;

  private int price;

  private String productCategory;

  @Column(nullable = false, unique = true)
  private String modelName;

  @Column(updatable = false, insertable = false,
          columnDefinition = "datetime default now()")
  private LocalDateTime regdate;
}