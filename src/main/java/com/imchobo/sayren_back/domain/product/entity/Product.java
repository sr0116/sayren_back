package com.imchobo.sayren_back.domain.product.entity;

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
public class Product {

    // 상품번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // 상품명
    @Column(nullable = false)
    private String name;

    // 판매가, 정상가
    @Column(nullable = false)
    private Integer price;

    // 보증금(위약금)
    private BigDecimal deposit;

    // 상품 상태 (ACTIVE/ DISABLED)
    @Column(updatable = false)
    private Boolean isUse = true;

    // 상품 설명(상세설명 포함)
    @Lob // (mediumtext로 되어있기때문에 Lob이 긴 텍스트 저장 컬럼)
    private String description;

    // 상품 등록시간 (기본값이 now)
    @Column(updatable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    // 상품 카테고리
    private String productCategory;

    // 상품 모델명(시리얼 넘버. 리스트 페이지랑 연결됨)
    private String modelName;

    // 상품 중심으로 태그 및 재고 한번에 관리
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductStock> stocks = new ArrayList<>();

}
