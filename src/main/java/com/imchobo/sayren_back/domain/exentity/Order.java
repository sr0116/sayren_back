package com.imchobo.sayren_back.domain.exentity;

import com.imchobo.sayren_back.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "tbl_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
/// ////////////   임시 //////////////////
  /** 주문 PK */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;

  /** 주문 회원 (FK → tbl_member) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  /** 주문 상태 (PENDING/PAID/SHIPPED/DELIVERED/CANCELED) */
  @Column(name = "status", nullable = false, length = 20)
  private String status;

  /** 배송지 ID (FK → tbl_address.addr_id) */
  @Column(name = "addr_id", nullable = false)
  private Long addrId;

  /** 주문 생성 시각 */
  @Column(name = "regdate", nullable = false)
  private LocalDateTime regdate;

  /** 주문 수정 시각 */
  @Column(name = "moddate")
  private LocalDateTime moddate;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  // === JPA 라이프사이클 ===
  @PrePersist
  public void onCreate() {
    this.regdate = LocalDateTime.now();
    if (this.status == null) {
      this.status = "PENDING"; // 기본값
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.moddate = LocalDateTime.now();
  }
}