package com.imchobo.sayren_back.domain.board.entity;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.common.en.CommonStatus;
//import com.imchobo.sayren_back.en.CategoryType;
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
    @Column(name = "category_id") // 제약조건: PK / auto_increment
    private Long id;

    // 대분류 카테고리 번호
    @ManyToOne(fetch = FetchType.LAZY) // 제약조건: FK
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    // 카테고리명
    @Column(nullable = false) // 제약조건: Not null
    private String name;
//
//    // 카테고리 타입
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false) // 제약조건: Not null
//    private CategoryType type;

    // 상태 (ACTIVE/ DISABLE/ DELETE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // 제약조건: Not null / default='ACTIVE'
    @Builder.Default
    private CommonStatus status = CommonStatus.ACTIVE;
}
