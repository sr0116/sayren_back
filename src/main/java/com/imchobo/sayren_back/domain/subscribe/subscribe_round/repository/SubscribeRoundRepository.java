package com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscribeRoundRepository extends JpaRepository<SubscribeRound, Long> {
  // 특정 구독에 연결된 모든 회차
  List<SubscribeRound> findBySubscribe(Subscribe subscribe);

  // 상태별 전체 회차 조회 (스케줄러에서 활용)
  List<SubscribeRound> findByPayStatus(PaymentStatus payStatus);

  // 특정 구독 + 상태별 조회 (페이징)
  Page<SubscribeRound> findBySubscribeAndPayStatus(Subscribe subscribe, PaymentStatus payStatus, Pageable pageable);

  // 가장 최신 회차 조회 (Optional)
  Optional<SubscribeRound> findTopBySubscribeOrderByRoundNoDesc(Subscribe subscribe);

  // 회차 정보
  Optional<SubscribeRound> findBySubscribeIdAndRoundNo(Long subscribeId, int roundNo);

  Subscribe subscribe(Subscribe subscribe);
  //이벤트 핸들러에서 사용
  List<SubscribeRound> findBySubscribeId(Long subscribeId);
  // 스케줄 처리
  List<SubscribeRound> findByDueDateAndPayStatus(LocalDate dueDate, PaymentStatus payStatus);

  // 관리자용 전체 조회
  @Query("SELECT sr FROM SubscribeRound sr " +
          "JOIN FETCH sr.subscribe s " +
          "JOIN FETCH s.member m " +
          "WHERE sr.payStatus = :status " +
          "ORDER BY sr.dueDate DESC")
  List<SubscribeRound> findAllByStatusWithMember(PaymentStatus status);

}