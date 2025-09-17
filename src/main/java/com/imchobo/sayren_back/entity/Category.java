package com.imchobo.sayren_back.domain.board.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    // 카테고리 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    // 대분류 카테고리 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    // 카테고리명
    @Column(nullable = false)
    private String name;

    // 카테고리 타입
    @Column(nullable = false)
    private String type;

    // 상태 (ACTIVE/ DISABLE/ DELETE)
    @Column(nullable = false)
    private String status = "ACTIVE";
}
