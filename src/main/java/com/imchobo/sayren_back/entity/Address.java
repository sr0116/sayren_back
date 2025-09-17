package com.imchobo.sayren_back.entity;

import jakarta.persistence.*;
import lombok.*;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;

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
  private Long addrId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 20)
  private String tel;

  @Column(nullable = false, length = 20)
  private String zipcode;

  @Column(name = "addr", nullable = false, length = 255)
  private String address;

  @Column(name = "is_default", nullable = false)
  private Boolean defaultAddress;

  @Column(length = 255)
  private String memo;
}
