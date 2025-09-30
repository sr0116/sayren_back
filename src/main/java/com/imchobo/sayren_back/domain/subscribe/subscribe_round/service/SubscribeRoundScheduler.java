package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.service.PaymentService;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundScheduler {

  private final SubscribeRepository subscribeRepository;
  private final PaymentService paymentService;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final PaymentMapper paymentMapper;

  // 매일 새벽 5시마다 실행
//  @Scheduled(cron = "0 0 5 * * *")
//  @Scheduled(fixedRate = 30000) // 테스트: 30초마다 실행
  @Transactional
  public void processDueRounds() {
    LocalDate today = LocalDate.now();
    List<SubscribeRound> dueRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);

    for (SubscribeRound round : dueRounds) {
      try {
        log.info("회차 결제 시도: subscribeId={}, roundNo={}, amount={}",
                round.getSubscribe().getId(), round.getRoundNo(), round.getAmount());

        PaymentResponseDTO prepareResult = paymentService.prepareForRound(round);

        // PortOne 연동 전이므로 fake impUid 사용
        String fakeImpUid = "test_imp_" + round.getId();
        paymentService.complete(prepareResult.getPaymentId(), fakeImpUid);

        round.setPayStatus(PaymentStatus.PAID);
        round.setPaidDate(LocalDateTime.now());

      } catch (Exception e) {
        log.error("회차 결제 실패: roundId={}, 이유={}", round.getId(), e.getMessage());
        round.setPayStatus(PaymentStatus.FAILED);
      }
    }
  }
}
