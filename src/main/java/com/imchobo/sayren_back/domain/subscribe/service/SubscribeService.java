package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto.SubscribeRoundResponseDTO;

import java.util.List;

public interface SubscribeService {
  // 구독 신청
  Subscribe createSubscribe(SubscribeRequestDTO dto, OrderItem orderItem);

  // 구독시 보증금 계산

  // 배송 완료시 상태 변경
  void  activateAfterDelivery(Long subscribeId, OrderItem orderItem);

  // 구독 단건 조회
  SubscribeResponseDTO getSubscribe(Long subscribeId);

  // 구독 전체 조회 (관리자용)
  List<SubscribeResponseDTO> getAll();

  // 구독 마이페이지 목록 조회
  List<SubscribeSummaryDTO> getSummaryList();

  // 구독 상태 변경(관리자용)
  void updateStatus(Long subscribeId, SubscribeStatus status);

  // (추가) 구독 취소 (사용자 요청)
  void cancelSubscribe(Long subscribeId);

// 구독 취소 (관리자 승인 여부)
  void processCancelRequest(Long subscribeId, boolean approved, ReasonCode reasonCode);

  // (추가) 구독 상태 변경 이력 조회
  List<SubscribeHistoryResponseDTO> getHistories(Long subscribeId);

  List<SubscribeRoundResponseDTO> getRoundBySubscribe(Long subscribeId);

  SubscribeRoundResponseDTO getRoundDetail(Long subscribeId, Integer roundNo);

}
