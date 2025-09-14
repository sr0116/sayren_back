package com.imchobo.sayren_back.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    // 카테고리 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    // 대분류 카테고리 번호
    private Integer parentCategoryId;

    // 카테고리명
    @Column(nullable = false)
    private String name;

    // 카테고리 타입
    @Column(nullable = false)
    private String type;

    // 상태 (ACTIVE/ DISABLE/ DELETE)
    @Column(nullable = false)
    private String status;
}
