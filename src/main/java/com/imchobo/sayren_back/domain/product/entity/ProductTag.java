package com.imchobo.sayren_back.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_product_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTag {

    // 상품 태그 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_tag_id")
    private Long id;

    // 태그명
    @Column(nullable = false)
    private String tagName;

    // 태그 값
    @Column(nullable = false)
    private String tagValue;

    // 연결
    @ManyToOne(fetch = FetchType.LAZY)  // 상품 하나에 대한 여러 태그
    @JoinColumn(name = "product_id")
    private Product product;

}
