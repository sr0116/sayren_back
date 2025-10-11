package com.imchobo.sayren_back.domain.delivery.address.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // FK (회원)

    @Column(nullable = false, length = 100)
    private String name; // 수령인 이름

    @Column(nullable = false, length = 20)
    private String tel; // 연락처

    @Column(nullable = false, length = 20)
    private String zipcode; // 우편번호

    @Column(name = "addr", nullable = false, length = 255)
    private String address; // 주소 (도로명 + 상세)

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false; // 기본 배송지 여부

    @Column(length = 255)
    private String memo; // 배송 메모
}
