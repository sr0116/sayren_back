package com.imchobo.sayren_back.domain.subscribe_payment.service;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe_payment.en.SubscribePaymentType;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import com.imchobo.sayren_back.domain.subscribe_payment.repository.SubscribePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribePaymentServiceImpl implements SubscribePaymentService {

  private final SubscribePaymentRepository subscribePaymentRepository;

  @Transactional
  @Override
  public void generateRounds(SubscribeResponseDTO subscribeDTO, Payment payment) {
    // DTO → 엔티티 변환 (subscribeId 로 Proxy 생성)
    Subscribe subscribeRef = Subscribe.builder()
            .subscribeId(subscribeDTO.getSubscribeId())
            .build();

    List<SubscribePayment> rounds = new ArrayList<>();

    for (int i = 1; i <= subscribeDTO.getTotalMonths(); i++) {
      SubscribePayment round;

      if (i == 1) {
        // 1회차: 보증금 + 첫 달 렌탈료
        round = SubscribePayment.builder()
                .subscribe(subscribeRef)
                .payment(payment)
                .roundNo(i)
                .type(SubscribePaymentType.DEPOSIT) // 보증금 포함된 첫 회차
                .amount(
                        Long.valueOf(subscribeDTO.getDepositSnapshot())
                                + Long.valueOf(subscribeDTO.getMonthlyFeeSnapshot())
                )
                .payStatus(PaymentStatus.PAID) // 첫 회차는 결제 완료
                .dueDate(LocalDate.now()) // 당일 결제
                .build();
      } else {
        // 2회차 이후: 월 렌탈료만
        round = SubscribePayment.builder()
                .subscribe(subscribeRef)
                .payment(payment)
                .roundNo(i)
                .type(SubscribePaymentType.MONTHLY)
                .amount(Long.valueOf(subscribeDTO.getMonthlyFeeSnapshot()))
                .payStatus(PaymentStatus.PENDING) // 대기 상태
                .dueDate(LocalDate.now().plusMonths(i - 1)) // i-1개월 뒤 납부일
                .build();
      }

      rounds.add(round);
    }

    subscribePaymentRepository.saveAll(rounds);
    log.info("구독 [{}] 의 결제 회차 {}개 생성 완료", subscribeDTO.getSubscribeId(), rounds.size());
  }
}