package com.imchobo.sayren_back.domain.subscribe.repository;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeHistoryRepository  extends JpaRepository<SubscribeHistory, Long> {
  // 특정 구독 히스토리 전체 조회
  List<SubscribeHistory> findBySubscribe_Id(Long subscribeId);

  // 상태 기준으로 조회
  List<SubscribeHistory> findByStatus(SubscribeStatus status);

  boolean existsBySubscribeAndReasonCode(Subscribe subscribe, ReasonCode reasonCode);

  Optional<Object> findFirstBySubscribeOrderByRegDateDesc(Subscribe subscribe);
}
