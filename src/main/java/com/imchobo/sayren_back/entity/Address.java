package com.imchobo.sayren_back.domain.delivery.entity;

import com.imchobo.sayren_back.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Address extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "addr_id")
  private Long addrId;// PK (NOT NULL, AUTO_INCREMENT(DB에서 자동증가))

  @Column(nullable = false)
  private Long memberId;// FK(어떤 테이블 참조하는지) → tbl_member.member_id (NOT NULL, 회원 번호 반드시 필요)


  @Column(nullable = false, length = 100)
  private String name; // 수령인 이름 (NOT NULL, 최대 100자)

  @Column(nullable = false, length = 20)
  private String tel; // 연락처 (NOT NULL, 최대 20자)

  @Column(nullable = false, length = 20)
  private String zipcode; // 우편번호 (NOT NULL, 최대 20자)

  @Column(name = "addr", nullable = false, length = 255)
  private String address; // 주소 상세 (NOT NULL, 최대 255자)

  @Column(name = "is_default", nullable = false)
  private Boolean defaultAddress;  // 기본 배송지 여부 (NOT NULL, 기본값 FALSE)

  @Column(length = 255)
  private String memo; // 배송 메모 (NULL 허용, 최대 255자)
}
