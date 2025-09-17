package com.imchobo.sayren_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_product_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock{

    // 재고 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_stock_id")
    private Long id;

    // 재고 수량
    @Column(nullable = false)
    private Integer stock;

    // 연결
    @OneToOne(fetch = FetchType.LAZY) // 상품 하나에 대한 여러 재고
    @JoinColumn(name = "product_id")  // 외래키 컬럼 이름지정
    private Product product;
}
