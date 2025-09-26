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
  private Long id;// PK (NOT NULL, AUTO_INCREMENT(DB에서 자동증가))

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 수령인 이름 (NOT NULL, 최대 100자)
  @Column(nullable = false, length = 100)
  private String name;

  // 연락처 (NOT NULL, 최대 20자)
  @Column(nullable = false, length = 20)
  private String tel;

  // 우편번호 (NOT NULL, 최대 20자)
  @Column(nullable = false, length = 20)
  private String zipcode;

  // 주소
  @Column(name = "addr", nullable = false)
  private String address;

  // 기본 배송지 여부 (NOT NULL, 기본값 FALSE)
  @Column(nullable = false)
  @Builder.Default
  private Boolean isDefault = false;

  // 배송 메모 (NULL 허용, 최대 255자)
  @Column(length = 255)
  private String memo;
}
