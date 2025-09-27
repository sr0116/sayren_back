package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.recorder.SubscribeHistoryRecorder;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeCreationException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeStatusInvalidException;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeHistoryMapper;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper.SubscribeRoundMapper;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribeRoundService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeServiceImpl implements SubscribeService {
  // DB 접근
  private final SubscribeRepository subscribeRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final DeliveryRepository deliveryRepository;
  private final DeliveryItemRepository deliveryItemRepository;
  // 매퍼
  private final SubscribeMapper subscribeMapper;
  private final SubscribeHistoryMapper subscribeHistoryMapper;
  private final SubscribeRoundMapper subscribeRoundMapper;
  // 서비스
  private final SubscribeRoundService subscribeRoundService;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final SubscribeHistoryRecorder subscribeHistoryRecorder;

  // 구독 테이블 생성
  @Transactional
  @Override
  public Subscribe createSubscribe(SubscribeRequestDTO dto, OrderItem orderItem) {

    //dto -> entity (기본값 세팅 PENDING_PAYMENT)
    Subscribe subscribe = subscribeMapper.toEntity(dto);
    // 보증금 및 월 렌탈료 저장
    Long productPrice = orderItem.getProductPriceSnapshot(); //상품 총 가격
    // 월렌탈료 먼저 계산
    Long monthlyFee = productPrice / dto.getTotalMonths();
    // 보증금 계산
    Long depositSnapshot = calculateDeposit(productPrice);
    // 스냅샷 값
    subscribe.setMonthlyFeeSnapshot(monthlyFee); // 렌탈료
    subscribe.setDepositSnapshot(depositSnapshot); // 보증금

    // 로그인 유저 주입
    Member currentMember = SecurityUtil.getMemberEntity(); // 또는 상위에서 받아온 member
    subscribe.setMember(currentMember);

    // 구독 저장
    Subscribe savedSubscribe = subscribeRepository.save(subscribe);

    // 회차 테이블 생성
    subscribeRoundService.createRounds(savedSubscribe, dto, orderItem);

    // 최초 상태(PENDING_PAYMENT) 기록
    subscribeHistoryRecorder.recordInit(savedSubscribe);

    subscribeStatusChanger.changeSubscribe(savedSubscribe, SubscribeTransition.PREPARE, ActorType.SYSTEM);
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
    return subscribeMapper.toResponseDTO(subscribe);
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
    Member member = SecurityUtil.getMemberEntity();
    List<Subscribe> subscribes = subscribeRepository.findByMemberId(member.getId());
    return subscribeMapper.toSummaryDTOList(subscribes);
  }

  // 배송 완료 후 상태 변경 (ACTIVE)
  @Transactional
  public void activateAfterDelivery(Long subscribeId, OrderItem orderItem) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    //   구독 준비중
    if (subscribe.getStatus() != SubscribeStatus.PREPARING) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }
    // 구독 개월 수 가져오기
    Integer months = subscribe.getOrderItem().getOrderPlan().getMonth();
    if (months == null || months <= 0) {
      throw new IllegalStateException("구독 기간(month)이 잘못 설정됨");
    }

    // 시작일/종료일 확정
//    LocalDate startDate = LocalDate.now();
//    subscribe.setStartDate(startDate);
//    subscribe.setEndDate(startDate.plusMonths(months)); // 총개월 수
//
//    // (나중에 배송 이벤트 처리 할거고 지금은 임시 )
//    // 상태 ACTIVE 전환 (이 안에서 save + event + history 기록까지 자동 처리)
//    // orderItem에서 deliveryItems를 통해 배송 추적
//    List<DeliveryItem> deliveryItems = deliveryItemRepository.findByOrderItem(orderItem);
//    Delivery delivery = deliveryItems.stream()
//            .map(DeliveryItem::getDelivery)
//            .findFirst()
//            .orElseThrow(() -> new DeliveryNotFoundException(orderItem.getId()));
//
//    if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
//      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.START, ActorType.SYSTEM);



//    // 회차 dueDate 확정
//    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribe(subscribe);
//    for (SubscribeRound round : rounds) {
//      round.setDueDate(startDate.plusMonths(round.getRoundNo() - 1));
//    }
//
//    log.info("구독 [{}] 활성화 완료. 시작일: {}, 종료일: {}, 총 {}회차 dueDate 확정",
//            subscribeId, startDate, subscribe.getEndDate(), rounds.size());
  }


  // 사용자 구독 취소 요청
  @Override
  @Transactional
  public void cancelSubscribe(Long subscribeId) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    // 이미 종료 되었거나 취소 상태시 예외 처리
    if (subscribe.getStatus() == SubscribeStatus.ENDED ||
            subscribe.getStatus() == SubscribeStatus.CANCELED) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }
    // 회원 취소 요청
    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.REQUEST_CANCEL, ActorType.USER);
  }

  //취소 승인/거절 (관리자)
  @Override
  @Transactional
  public void processCancelRequest(Long subscribeId, boolean approved, ReasonCode reasonCode) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));
    if (subscribe.getStatus() != SubscribeStatus.ACTIVE) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }
    if (approved) {
      // 승인 처리시
      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_APPROVE, ActorType.ADMIN);
    } else {
      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_REJECT, ActorType.ADMIN);
    }
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
}
