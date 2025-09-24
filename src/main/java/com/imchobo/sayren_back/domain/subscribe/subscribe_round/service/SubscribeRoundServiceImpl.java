package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper.SubscribeRoundMapper;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundServiceImpl implements SubscribeRoundService {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeRoundMapper subscribeRoundMapper;

  // 구독회차 정보
  @Transactional
  @Override
  public void createRounds(Subscribe subscribe, SubscribeRequestDTO dto) {
    // 구독 시작일
    LocalDate startDate = subscribe.getStartDate();
    Long monthlyFee = subscribe.getMonthlyFeeSnapshot();
    Long deposit = subscribe.getDepositSnapshot();
    int totalMonths = dto.getTotalMonths();

    for (int i = 1; i <= totalMonths; i++) {
      SubscribeRound round = new SubscribeRound();
      round.setSubscribe(subscribe);
      round.setRoundNo(i);
//      round.setPayStatus(PaymentStatus.PENDING); // 결제 대기 이미 default 라 나중에 삭제
      // 1회차일 때 보증금 + 월 렌탈료 포함
      if (i == 1) {
        round.setAmount((long) (monthlyFee + deposit)); // 임시로 형 변환 해두고 디비 타입 바꿀지 아니면 형변환으로 사용할지 생각
      } else {
        round.setAmount(monthlyFee);
      }
      round.setDueDate(startDate.plusMonths(i - 1));

      subscribeRoundRepository.save(round);
      // 확인용

      log.info("구독 [{}] - {}회차 생성 완료 (금액: {}, 납부예정일: {})",
              subscribe.getId(), i, round.getAmount(), round.getDueDate());
    }
  }
}