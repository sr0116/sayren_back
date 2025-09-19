package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper.SubscribeRoundMapper;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundServiceImpl implements SubscribeRoundService {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeRoundMapper subscribeRoundMapper;


  // 구독회차 정보
  @Override
  public void createRounds(Subscribe subscribe, SubscribeRequestDTO dto) {
    // 구독 시작일
    LocalDate startDate = subscribe.getStartDate();
    int totalMonths = dto.getTotalMonths();
    for (int i = 1; i <= totalMonths; i++) {
      SubscribeRound round = new SubscribeRound();
      round.setSubscribe(subscribe);
      round.setRoundNo(i);
//      round.setPayStatus(PaymentStatus.PENDING); // 결제 대기 이미 default 라 나중에 삭제
      round.setDueDate(startDate.plusMonths(i - 1));
      subscribeRoundRepository.save(round);
      // 확인용
      log.info("구독 [{}] - {}회차 생성 완료 (납부 예정일: {})",
              subscribe.getId(), i, round.getDueDate());

    }
  }
}