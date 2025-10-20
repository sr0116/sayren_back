package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
  // 로그인 사용자 기준 정렬 조회
  List<RefundRequest> findByMemberOrderByRegDateDesc(Member member);

  List<RefundRequest> findByMember_Id(Long memberId);

  // 관리자 전체 조회용
  List<RefundRequest> findAllByOrderByRegDateDesc();


  boolean existsByOrderItemAndStatusIn(OrderItem orderItem, Collection<RefundRequestStatus> statuses);

  Optional<RefundRequest> findFirstByOrderItemOrderByRegDateDesc(OrderItem orderItem);

  Optional<RefundRequest> findByOrderItem(OrderItem orderItem);
  List<RefundRequest> findAllByOrderItem(OrderItem orderItem);

  // 관리자용 환불 요청 전체 조회(회원 정보까지)
  @Query("SELECT r FROM RefundRequest r " +
          "JOIN FETCH r.member m " +
          "JOIN FETCH r.orderItem oi " +
          "JOIN FETCH oi.order o " +
          "ORDER BY r.regDate DESC")
  List<RefundRequest> findAllWithMemberAndOrder();

  Optional<RefundRequest> findFirstByOrderItemIdOrderByRegDateDesc(Long orderItemId);



}
