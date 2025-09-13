package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;

import java.util.List;

public interface SubscribeService {
  // 구독 신청
  SubscribeResponseDTO create(SubscribeRequestDTO dto);

  // 구독 단건 조회
  SubscribeResponseDTO getById(Long subscribeId);

  // 구독 전체 조회
  List<SubscribeResponseDTO> getAll();

  // 구독 마이페이지 목록 조회
  List<SubscribeSummaryDTO> getSummaryList();

  // 구독 상태 변경(총 6개)
  void updateStatus(Long subscribeId, SubscribeStatus status);

}
