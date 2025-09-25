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
import java.util.ArrayList;
import java.util.List;

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

    List<SubscribeRound> rounds = new ArrayList<>();

    for (int i = 1; i <= totalMonths; i++) {
      Long amount = (i == 1) ? monthlyFee + deposit // 1회차 = 월 렌탈료 + 보증금
              : monthlyFee;          // 나머지 회차 = 월 렌탈료만

      SubscribeRound round = SubscribeRound.builder().subscribe(subscribe).roundNo(i).amount(amount).dueDate(startDate.plusMonths(i - 1)).build();

      rounds.add(round);
    }
    subscribeRoundRepository.saveAll(rounds);
    // 확인용
    log.info("구독 [{}] - 총 {}회차 생성 완료. (1회차 금액: {}, 보증금 포함, 시작일: {})", subscribe.getId(), totalMonths, rounds.get(0).getAmount(), startDate);
  }
}
