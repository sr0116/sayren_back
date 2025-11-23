package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.exception.DeliveryNotFoundException;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.calculator.RentalPriceCalculator;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundRequestEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentHistoryRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeCancelHandler;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeEventHandler;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeActivatedEvent;
import com.imchobo.sayren_back.domain.subscribe.dto.*;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.ActiveSubscriptionException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeStatusInvalidException;
import com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round.SubscribeRoundNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeHistoryMapper;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto.SubscribeRoundResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper.SubscribeRoundMapper;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribeRoundService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeServiceImpl implements SubscribeService {
  // DB 접근
  private final SubscribeRepository subscribeRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  // 매퍼
  private final SubscribeMapper subscribeMapper;
  private final SubscribeHistoryMapper subscribeHistoryMapper;
  // 서비스
  private final SubscribeRoundService subscribeRoundService;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final SubscribeEventHandler subscribeEventHandler;
  private final DeliveryItemRepository deliveryItemRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeRoundMapper subscribeRoundMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final SubscribeCancelHandler subscribeCancelHandler;
  private final RefundRequestRepository refundRequestRepository;
  private final RentalPriceCalculator rentalPriceCalculator;
  private final PaymentRepository paymentRepository;
  private final PaymentHistoryRepository paymentHistoryRepository;
  private final RefundRepository refundRepository;


  // 구독 테이블 생성
  @Transactional
  @Override
  public Subscribe createSubscribe(SubscribeRequestDTO dto, OrderItem orderItem) {

    //dto -> entity (기본값 세팅 PENDING_PAYMENT)
    Subscribe subscribe = subscribeMapper.toEntity(dto);
    // 보증금 및 월 렌탈료 저장
    Long productPrice = orderItem.getProductPriceSnapshot(); // 상품 총 가격
    RentalPriceDTO rentalPrice = rentalPriceCalculator.calculate(productPrice, dto.getTotalMonths());

    // 월렌탈료
    subscribe.setMonthlyFeeSnapshot(rentalPrice.getMonthlyFee());
    // 보증금
    subscribe.setDepositSnapshot(rentalPrice.getDeposit());

    // 로그인 유저 주입
    Member currentMember = SecurityUtil.getMemberEntity(); // 또는 상위에서 받아온 member
    subscribe.setMember(currentMember);

    // 구독 저장
    Subscribe savedSubscribe = subscribeRepository.saveAndFlush(subscribe);

    // 회차 테이블 생성
    subscribeRoundService.createRounds(savedSubscribe, dto, orderItem);

    // 최초 상태(PENDING_PAYMENT) 기록
    subscribeEventHandler.recordInit(savedSubscribe);

    return savedSubscribe;
  }

  // 구독 단건 조회
  @Override
  @Transactional(readOnly = true)
  public SubscribeResponseDTO getSubscribe(Long subscribeId) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(SubscribeNotFoundException::new);

    SubscribeResponseDTO dto = subscribeMapper.toResponseDTO(subscribe);

    subscribeHistoryRepository.findFirstBySubscribeOrderByRegDateDesc(subscribe)
            .ifPresent(history -> dto.setReasonCode(history.getReasonCode()));

    return dto;
  }

  // 구독 전체 조회(관리자용)
  @Override
  @Transactional(readOnly = true)
  public List<SubscribeResponseDTO> getAll() {
    List<Subscribe> subscribes = subscribeRepository.findAll();
    return subscribeMapper.toResponseDTOList(subscribes);
  }

  // 구독 마이페이지 목록 조회(로그인 회원 기준)
  @Override
  @Transactional(readOnly = true)
  public List<SubscribeSummaryDTO> getSummaryList() {
    Member currentMember = SecurityUtil.getMemberEntity();
    log.info("구독 내역 조회 - memberId={}", currentMember.getId());

    List<Subscribe> subscribes = subscribeRepository.findAllWithRefundByMember(currentMember.getId());
    log.info("조회된 구독 건수={}", subscribes.size());

    List<SubscribeSummaryDTO> dtos = subscribeMapper.toSummaryDTOList(subscribes);

    for (int i = 0; i < subscribes.size(); i++) {
      Subscribe s = subscribes.get(i);
      SubscribeSummaryDTO dto = dtos.get(i);

      //  (1) 최신 구독 이력 조회 (단방향)
      subscribeHistoryRepository.findLatestBySubscribeId(s.getId())
              .ifPresent(history -> {
                if (history.getReasonCode() != null)
                  dto.setReasonCode(history.getReasonCode());
                if (history.getStatus() != null)
                  dto.setStatus(history.getStatus());
              });

      //  (2) 환불 요청 상태/사유 반영
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(s.getOrderItem())
              .ifPresent(req -> {
                if (req.getStatus() != null)
                  dto.setRefundRequestStatus(req.getStatus());
                if (req.getReasonCode() != null && dto.getReasonCode() == null)
                  dto.setReasonCode(req.getReasonCode());
              });

      //  (3) 상태 보정
      if (dto.getStatus() == null && s.getStatus() != null)
        dto.setStatus(s.getStatus());
    }

    return dtos;
  }

  // 구독 회차 정보 리스트 조회
  @Transactional
  @Override
  public List<SubscribeRoundResponseDTO> getRoundBySubscribe(Long subscribeId) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribeId);
    return subscribeRoundMapper.toResponseDTOS(rounds);
  }

  // 구독 회차 정보 단일 조회
  @Transactional
  @Override
  public SubscribeRoundResponseDTO getRoundDetail(Long subscribeId, Integer roundNo) {
    SubscribeRound round = subscribeRoundRepository.findBySubscribeIdAndRoundNo(subscribeId, roundNo)
            .orElseThrow(() -> new SubscribeRoundNotFoundException(roundNo));
    return subscribeRoundMapper.toDto(round);
  }

  // 관리자: 전체 구독 조회
  @Override
  @Transactional(readOnly = true)
  public List<SubscribeResponseDTO> getAllForAdmin() {
    //  기본 구독 목록 조회
    List<Subscribe> subscribes = subscribeRepository.findAllWithMemberOrderAndRefund();
    log.info("[ADMIN] 구독 엔티티 수: {}", subscribes.size());

    if (subscribes.isEmpty()) return List.of();

    // 구독 ID 리스트 추출
    List<Long> subscribeIds = subscribes.stream()
            .map(Subscribe::getId)
            .toList();

    //  구독별 모든 이력 조회
    List<SubscribeHistory> allHistories =
            subscribeHistoryRepository.findAllBySubscribeIds(subscribeIds);

    // 구독별로 이력 그룹핑
    Map<Long, List<SubscribeHistory>> historyMap = allHistories.stream()
            .collect(Collectors.groupingBy(h -> h.getSubscribe().getId()));

    //  DTO 변환 및 이력 병합
    List<SubscribeResponseDTO> dtos = subscribes.stream()
            .map(subscribe -> {
              SubscribeResponseDTO dto = subscribeMapper.toResponseDTO(subscribe);

              // 이력 매핑
              List<SubscribeHistory> histories = historyMap.get(subscribe.getId());
              if (histories != null && !histories.isEmpty()) {
                log.debug(" [HISTORY] subscribeId={}, count={}", subscribe.getId(), histories.size());
                histories.forEach(h ->
                        log.trace("   ↳ reason={}, status={}, regDate={}",
                                h.getReasonCode(),
                                h.getStatus(),
                                h.getRegDate())
                );

                // 프론트 기본값용 최신 이력 1건 반영
                SubscribeHistory latest = histories.get(0);
                dto.setStatus(latest.getStatus());
                dto.setReasonCode(latest.getReasonCode());
              }

              // 최신 환불 요청 상태 추가
              refundRequestRepository.findFirstByOrderItemIdOrderByRegDateDesc(subscribe.getOrderItem().getId())
                      .ifPresent(refund -> dto.setRefundRequestStatus(refund.getStatus()));

              return dto;
            })
            .collect(Collectors.toList());

    log.info(" [ADMIN] 최종 DTO 수: {}", dtos.size());
    return dtos;
  }



  // 배송 완료 후 상태 변경 (ACTIVE)
  @Transactional
  @Override
  public void activateAfterDelivery(Long subscribeId, OrderItem orderItem) {
    log.info("현재 트랜잭션 활성화 여부={}", TransactionSynchronizationManager.isActualTransactionActive());

    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    //   구독 준비중
    if (subscribe.getStatus() == SubscribeStatus.PENDING_PAYMENT) {
      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.PREPARE, ActorType.SYSTEM);
      log.info("자동 결제 완료 처리 → 상태 전환: PENDING_PAYMENT → PREPARING");
    }
    //  준비 상태가 아니라면 예외
    if (subscribe.getStatus() != SubscribeStatus.PREPARING) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }

    // orderItem을 통한 배송 상태 확인
    List<DeliveryItem> deliveryItems = deliveryItemRepository.findByOrderItem(orderItem);
    Delivery delivery = deliveryItems.stream()
            .map(DeliveryItem::getDelivery)
            .findFirst()
            .orElseThrow(() -> new DeliveryNotFoundException(orderItem.getId()));

    // 배송 완료 상태가 되어야만 구독 활성화 이벤트가 발행됨
    if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
      eventPublisher.publishEvent(new SubscribeActivatedEvent(subscribeId, LocalDate.now()));
      log.info("[SERVICE] 배송 완료 감지 → 구독 활성화 이벤트 발행 완료 (subscribeId={})", subscribeId);
    } else {
      log.info("[SERVICE] 배송 상태가 DELIVERED가 아니므로 활성화 이벤트 발행 생략 (status={})", delivery.getStatus());
    }
    // 이벤트 쪽에서 시작일 확정
  }

  // 배송 회수 완료 후 상태 변경
  @Transactional
  @Override
  public void cancelAfterReturn(Long subscribeId, OrderItem orderItem) {
    log.info("현재 트랜잭션 활성화 여부={}", TransactionSynchronizationManager.isActualTransactionActive());

    // 구독 엔티티 조회
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    // 배송 조회
    List<DeliveryItem> deliveryItems = deliveryItemRepository.findByOrderItem(orderItem);
    Delivery delivery = deliveryItems.stream()
            .map(DeliveryItem::getDelivery)
            .findFirst()
            .orElseThrow(() -> new DeliveryNotFoundException(orderItem.getId()));

    // 회수 조건
    if(delivery.getStatus() != DeliveryStatus.RETURNED && delivery.getType() != DeliveryType.RETURN) {
      throw new IllegalStateException("배송 상태가 RETURNED가 아닙니다.현재 상태=" + delivery.getStatus());
    }

    // 구독 상태 검증
    if (subscribe.getStatus() == SubscribeStatus.CANCELED ||
            subscribe.getStatus() == SubscribeStatus.ENDED) {
      log.warn("이미 종료된 구독입니다. 상태={}", subscribe.getStatus());
      return;
    }
    // 상태 변경
    subscribeStatusChanger.changeSubscribe(
            subscribe,
            SubscribeTransition.RETURNED_AND_CANCELED,
            ActorType.SYSTEM);
    log.info("배송 회수 완료 처리 → 구독 [{}] 상태 전환: {} → RETURNED_AND_CANCELED",
            subscribeId, subscribe.getStatus());
  }

  // 사용자 구독 취소 요청
  // SubscribeServiceImpl.java

  @Override
  @Transactional
  public void cancelSubscribe(Long subscribeId, ReasonCode userReason) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    if (subscribe.getStatus() == SubscribeStatus.CANCELED
            || subscribe.getStatus() == SubscribeStatus.ENDED) {
      throw new SubscribeStatusInvalidException("이미 취소된 구독입니다.");
    }

    //  사유가 비어 있으면 USER_REQUEST 기본값
    if (userReason == null) {
      userReason = ReasonCode.USER_REQUEST;
    }

    //  구독 상태를 REQUEST_CANCEL 로 변경
    subscribeStatusChanger.changeSubscribe(
            subscribe,
            SubscribeTransition.REQUEST_CANCEL,
            userReason,
            ActorType.USER
    );

    log.info("[USER ACTION] 구독 취소 요청 → subscribeId={}, reason={}", subscribeId, userReason);
  }

  //취소 승인/거절 (관리자)
  @Override
  @Transactional
  public void processCancelRequest(Long subscribeId, RefundRequestStatus status, ReasonCode reasonCode) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    // ACTIVE 상태만 처리 가능
    if (subscribe.getStatus() != SubscribeStatus.ACTIVE) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }

    // 관리자 승인 처리
    if (status == RefundRequestStatus.APPROVED) {

      // (A) 계약 만료(EXPIRED): 구독 종료 + 환불 요청 생성
      if (reasonCode == ReasonCode.EXPIRED) {
        // 1) 구독 상태를 ENDED로 전환
        subscribeStatusChanger.changeSubscribe(
                subscribe,
                SubscribeTransition.END,
                reasonCode,
                ActorType.ADMIN
        );

        // 2) 환불 요청 테이블 생성 (보증금 환불용)
        RefundRequest refundRequest = RefundRequest.builder()
                .orderItem(subscribe.getOrderItem())
                .member(subscribe.getMember())
                .status(RefundRequestStatus.APPROVED_WAITING_RETURN) // 회수 완료 후 환불 대기
                .reasonCode(ReasonCode.EXPIRED)
                .build();

        refundRequestRepository.saveAndFlush(refundRequest);

        // 3) 환불 이벤트 발행 (RefundEventHandler → RefundService 연계)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            eventPublisher.publishEvent(new RefundRequestEvent(
                    subscribe.getOrderItem().getId(),
                    subscribe.getId(),
                    RefundRequestStatus.APPROVED_WAITING_RETURN,
                    ReasonCode.EXPIRED,
                    ActorType.ADMIN
            ));
            log.info("[EVENT][AFTER_COMMIT] 계약 만료 환불 이벤트 발행 → subscribeId={}, reason=EXPIRED", subscribeId);
          }
        });

        log.info("[ADMIN ACTION] 계약 만료 승인 + 환불 요청 생성 완료 → subscribeId={}, status=ENDED", subscribeId);
        return;
      }

      // (B) 일반적인 환불 승인 (취소, 불량, 배송 문제 등)
      RefundRequest request = RefundRequest.builder()
              .orderItem(subscribe.getOrderItem())
              .member(subscribe.getMember())
              .status(RefundRequestStatus.APPROVED_WAITING_RETURN) // 회수 대기
              .reasonCode(reasonCode)
              .build();

      refundRequestRepository.saveAndFlush(request);

      // 트랜잭션 커밋 후 이벤트 발행 (회수 및 환불 로직 연계)
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          eventPublisher.publishEvent(new RefundRequestEvent(
                  subscribe.getOrderItem().getId(),
                  subscribe.getId(),
                  RefundRequestStatus.APPROVED_WAITING_RETURN,
                  reasonCode,
                  ActorType.ADMIN
          ));
          log.info("[EVENT][AFTER_COMMIT] 구독 환불 승인 이벤트 발행 → subscribeId={}, status=APPROVED_WAITING_RETURN", subscribeId);
        }
      });
      return;
    }

    // 관리자 거절 처리
    if (status == RefundRequestStatus.REJECTED) {
      subscribeStatusChanger.changeSubscribe(
              subscribe,
              SubscribeTransition.CANCEL_REJECT,
              ActorType.ADMIN
      );
      log.info("[ADMIN ACTION] 구독 취소 요청 거절 처리 완료 → subscribeId={}", subscribeId);
      return;
    }

    //  기타 상태 (보류, 취소 등)
    subscribeCancelHandler.handle(subscribe, status, reasonCode);
  }


  // 구독 상태 변경 이력 조회
  @Override
  @Transactional
  public List<SubscribeHistoryResponseDTO> getHistories(Long subscribeId) {
    List<SubscribeHistory> histories = subscribeHistoryRepository.findBySubscribe_Id(subscribeId);
    if (histories.isEmpty()) {
      throw new SubscribeNotFoundException(subscribeId);
    }
    return subscribeHistoryMapper.toResponseDTOList(histories);
  }

  // 구독 상태 변경 (공용 메서드)
  @Override
  @Transactional
  public void updateStatus(Long subscribeId, SubscribeStatus status) {
    Subscribe entity = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));
    entity.setStatus(status);
    subscribeRepository.save(entity);
  }

  // 구독 삭제
  @Transactional
  @Override
  public void deleteSubscribe(Long subscribeId) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    // 1. 구독 상태 검증
    if (List.of(
            SubscribeStatus.PREPARING,
            SubscribeStatus.ACTIVE,
            SubscribeStatus.PENDING_PAYMENT,
            SubscribeStatus.OVERDUE
    ).contains(subscribe.getStatus())) {
      throw new SubscribeStatusInvalidException("진행 중인 구독은 삭제할 수 없습니다.");
    }

    // 2. 환불 요청 진행 중인 경우 삭제 불가
    boolean hasRefundInProgress = refundRequestRepository.existsByOrderItemAndStatusIn(
            subscribe.getOrderItem(),
            List.of(
                    RefundRequestStatus.PENDING,
                    RefundRequestStatus.APPROVED_WAITING_RETURN
            )
    );
    if (hasRefundInProgress) {
      throw new SubscribeStatusInvalidException("환불이 진행 중인 구독은 삭제할 수 없습니다.");
    }

    // 3. 구독 회차 조회
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribeId);

    // 3-1. 각 회차에 연결된 결제 및 결제 관련 데이터 삭제
    for (SubscribeRound round : rounds) {
      List<Payment> payments = paymentRepository.findBySubscribeRound(round);

      for (Payment payment : payments) {
        // 결제 이력 삭제
        paymentHistoryRepository.deleteAllByPayment(payment);

        // 환불 내역 삭제
        refundRepository.deleteAllByPayment(payment);

        // 결제 삭제
        paymentRepository.delete(payment);
        log.info("[DELETE] 결제 삭제 완료 (paymentId={})", payment.getId());
      }
    }

    // 4. 구독 회차 삭제
    if (!rounds.isEmpty()) {
      subscribeRoundRepository.deleteAll(rounds);
      log.info("[DELETE] 구독 회차 {}건 삭제 완료 (subscribeId={})", rounds.size(), subscribeId);
    }

    // 5. 구독 상태 이력 삭제
    subscribeHistoryRepository.deleteAllBySubscribe(subscribe);
    log.info("[DELETE] 구독 이력 삭제 완료 (subscribeId={})", subscribeId);

    // 6. 구독 삭제
    subscribeRepository.delete(subscribe);
    log.info("[DELETE] 구독 삭제 완료 (subscribeId={})", subscribeId);
  }



  // 구독 중인 상품 존재 여부 확인 (관리자 같은 경우에는 멤버 아이디로)
  @Transactional(readOnly = true)
  @Override
  public void validateNoActiveSubscription(Long memberId) {
    List<SubscribeStatus> activeStatuses = List.of(
            SubscribeStatus.ACTIVE,
            SubscribeStatus.PREPARING,
            SubscribeStatus.PENDING_PAYMENT
    );

    boolean exists = subscribeRepository.existsByMember_IdAndStatusIn(memberId, activeStatuses);
    if (exists) {
      throw new ActiveSubscriptionException(memberId);
    }
  }
// 멤버
  @Transactional(readOnly = true)
  @Override
  public void validateNoActiveSubscription() {
    validateNoActiveSubscription(SecurityUtil.getMemberAuthDTO().getId());
  }

}
