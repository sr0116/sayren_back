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
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundRequestEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
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
import java.util.List;

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

  // 보증금 계산(일단 20% 고정 임시로 나중에 % 수정 가능성)
  private Long calculateDeposit(Long monthlyFee) {
    if (monthlyFee == null) return 0L;
    return Math.round(monthlyFee * 0.2);
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
  @Transactional
  public List<SubscribeSummaryDTO> getSummaryList() {
    Member currentMember = SecurityUtil.getMemberEntity();
    log.info("구독 내역 조회 - memberId={}", currentMember.getId());

    // 기존: findByMemberId(member.getId())
    List<Subscribe> subscribes = subscribeRepository.findByMember(currentMember);

    log.info("조회된 구독 건수={}", subscribes.size());
    return subscribeMapper.toSummaryDTOList(subscribes);
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
    List<Subscribe> subscribes = subscribeRepository.findAllWithMemberAndOrder();
    List<SubscribeResponseDTO> dtos = subscribeMapper.toResponseDTOList(subscribes);

    for (int i = 0; i < subscribes.size(); i++) {
      Subscribe s = subscribes.get(i);
      SubscribeResponseDTO dto = dtos.get(i);

      subscribeHistoryRepository.findFirstBySubscribeOrderByRegDateDesc(s)
              .ifPresent(history -> dto.setReasonCode(history.getReasonCode()));
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(s.getOrderItem())
              .ifPresent(req -> dto.setRefundRequestStatus(req.getStatus()));
    }
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
  @Override
  @Transactional
  public void cancelSubscribe(Long subscribeId) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    // 이미 종료 되었거나 취소 상태시 예외 처리
    if (subscribe.getStatus() == SubscribeStatus.ACTIVE
            && subscribeHistoryRepository.existsBySubscribeAndReasonCode(subscribe, ReasonCode.USER_REQUEST)) {
      throw new SubscribeStatusInvalidException("이미 취소 요청 중인 구독입니다.");
    }
    if (subscribe.getStatus() == SubscribeStatus.CANCELED
            || subscribe.getStatus() == SubscribeStatus.ENDED ){
      throw new SubscribeStatusInvalidException("이미 취소 요청된 구독입니다.");
    }
    // 회원 취소 요청
    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.REQUEST_CANCEL, ActorType.USER);

  }

  //취소 승인/거절 (관리자)
  @Override
  @Transactional
  public void processCancelRequest(Long subscribeId, RefundRequestStatus status, ReasonCode reasonCode) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    // 일단 구독 상태가 구독 중이어야 함
    if (subscribe.getStatus() != SubscribeStatus.ACTIVE) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }
    // 관리자가 승인시
    if (status == RefundRequestStatus.APPROVED) {
      //환불 요청 자동 생성
      RefundRequest request = RefundRequest.builder()
              .orderItem(subscribe.getOrderItem())
              .member(subscribe.getMember())
              .status(RefundRequestStatus.APPROVED_WAITING_RETURN) // 환불 승인 및 회수 중
              .reasonCode(reasonCode)
              .build();

      refundRequestRepository.saveAndFlush(request);
      // 트랜잭션 커밋 후 이벤트 발행 (핸들러에서 이후 회수/환불 처리)
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
    // 거절시 상태 복원 처리
    if (status == RefundRequestStatus.REJECTED) {
      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_REJECT, ActorType.ADMIN);
      return;
    }
    //  기타 상태 처리 (보류,취소 등)
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


  // 사용자 같은 경우 시큐리티 쪽에서 멤버 가져오기
  @Transactional(readOnly = true)
  public void validateNoActiveSubscriptionForCurrentUser() {
    Member currentMember = SecurityUtil.getMemberEntity();

    List<SubscribeStatus> activeStatuses = List.of(
            SubscribeStatus.ACTIVE,
            SubscribeStatus.PREPARING,
            SubscribeStatus.PENDING_PAYMENT
    );

    boolean exists = subscribeRepository.existsByMember_IdAndStatusIn(currentMember.getId(), activeStatuses);
    if (exists) {
      throw new ActiveSubscriptionException();
    }
  }
}
