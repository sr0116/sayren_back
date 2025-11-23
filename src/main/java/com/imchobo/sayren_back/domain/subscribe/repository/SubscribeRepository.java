package com.imchobo.sayren_back.domain.subscribe.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

  // 구독 아이템 기준으로 구독 조회(1:1 관계로 확인시에)
  Optional<Subscribe> findByOrderItem_Id(Long orderItemId);

  Optional<Subscribe> findByOrderItem(OrderItem orderItem);

  //  관리자: 구독 + 회원 + 주문 + 환불까지 (이력은 별도 조회)
  @Query("""
        SELECT DISTINCT s
        FROM Subscribe s
        JOIN FETCH s.member m
        JOIN FETCH s.orderItem oi
        LEFT JOIN FETCH oi.order o
        LEFT JOIN FETCH RefundRequest rr ON rr.orderItem = oi
        ORDER BY s.id DESC
    """)
  List<Subscribe> findAllWithMemberOrderAndRefund();

  @Query("""
          SELECT DISTINCT s
          FROM Subscribe s
          JOIN FETCH s.member m
          JOIN FETCH s.orderItem oi
          LEFT JOIN FETCH oi.product p
          LEFT JOIN FETCH RefundRequest rr ON rr.orderItem = oi
          WHERE m.id = :memberId
          ORDER BY s.id DESC
          """)
  List<Subscribe> findAllWithRefundByMember(@Param("memberId") Long memberId);


  // 구독 상태 여부
  boolean existsByMember_IdAndStatusIn(Long memberId, List<SubscribeStatus> statuses);

  boolean existsByOrderItem_Id(Long orderItemId);

}
