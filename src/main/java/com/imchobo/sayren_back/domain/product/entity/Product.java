package com.imchobo.sayren_back.domain.product.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.sun.source.tree.CaseTree;
import jakarta.persistence.*;
import lombok.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product extends CreatedEntity {

    // 상품번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    // 상품명
    @Column(nullable = false)
    private String name;

    // 판매가, 정상가
    @Column(nullable = false)
    private Integer price;

    // 상품 상태
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isUse = false;

    // 상품 설명(상세설명 포함)
    @Lob // (mediumtext로 되어있기때문에 Lob이 긴 텍스트 저장 컬럼)
    @Column(nullable = false)
    private String description;

    // 상품 카테고리
    @Column(nullable = false)
    private String productCategory;

    // 상품 모델명(시리얼 넘버. 리스트 페이지랑 연결됨)
    @Column(nullable = false, unique = true)
    private String modelName;

}
