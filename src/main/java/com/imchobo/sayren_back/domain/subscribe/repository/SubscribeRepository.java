package com.imchobo.sayren_back.domain.subscribe.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
  // 멤버 아이디 조회(마이페이지)
  List<Subscribe> findByMember_Id(Long memberId);


  List<Subscribe> findByMember(Member member);

  // 특정 회원의 구독 상태별 조회
  List<Subscribe> findByMember_IdAndStatus(Long memberId, SubscribeStatus status);

  // 구독 아이템 기준으로 구독 조회(1:1 관계로 확인시에)
  Optional<Subscribe> findByOrderItem_Id(Long orderItemId);

  List<Subscribe> findByMemberId(Long memberId);

  Optional<Subscribe> findByOrderItem(OrderItem orderItem);

  // 관리자용 전체 조회
  @Query("""
            SELECT s FROM Subscribe s
            JOIN FETCH s.member m
            JOIN FETCH s.orderItem oi
            JOIN FETCH oi.order o
            ORDER BY s.id DESC
          """)
  List<Subscribe> findAllWithMemberAndOrder();

  // 구독 상태 여부
  boolean existsByMember_IdAndStatusIn(Long memberId, List<SubscribeStatus> statuses);


}
