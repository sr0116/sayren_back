package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeActivatedEvent;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscribeActivatedEventHandler {

  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;

  @PostConstruct
  public void init() {
    log.info("SubscribeActivatedEventHandler 빈 등록 완료");
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onSubscribeActivated(SubscribeActivatedEvent event) {
    Long subscribeId = event.getSubscribeId();
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new IllegalStateException("구독을 찾을 수 없습니다: " + subscribeId));

    LocalDate startDate = event.getStartDate();
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribe(subscribe);

    if (rounds.isEmpty()) {
      log.warn("[EVENT] 회차 정보 없음 → dueDate 계산 생략 → subscribeId={}", subscribeId);
      return;
    }

    for (SubscribeRound round : rounds) {
      round.setDueDate(startDate.plusMonths(round.getRoundNo() - 1));
    }

    subscribeRoundRepository.saveAll(rounds);

    log.info("[EVENT] 구독 활성화 후 회차 dueDate 초기화 완료 → subscribeId={}, 총 {}회차",
            subscribe.getId(), rounds.size());
  }
}
