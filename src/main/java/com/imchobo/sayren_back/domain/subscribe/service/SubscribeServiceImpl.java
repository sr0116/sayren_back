package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeCreationException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeStatusInvalidException;
import com.imchobo.sayren_back.domain.payment.component.recorder.HistoryRecorder;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeHistoryMapper;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.mapper.SubscribeRoundMapper;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribeRoundService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {
  // DB 접근
  private final SubscribeRepository subscribeRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;

  // 매퍼
  private final SubscribeMapper subscribeMapper;
  private final SubscribeHistoryMapper subscribeHistoryMapper;
  private final SubscribeRoundMapper subscribeRoundMapper;

  // 서비스
  private final SubscribeRoundService subscribeRoundService;
  private final HistoryRecorder historyRecorder;

  // 구독 테이블 생성
  @Transactional
  @Override
  public Subscribe createSubscribe(SubscribeRequestDTO dto) {
    try {
      //dto -> entity (기본값 세팅 PENDING_PAYMENT)
      Subscribe subscribe = subscribeMapper.toEntity(dto);

      // 보증금 및 월 렌탈료 저장
      int monthlyFee = dto.getMonthlyFeeSnapshot();
      int depositSnapshot = calculateDeposit(monthlyFee);

      // 스냅샷 값
      subscribe.setMonthlyFeeSnapshot((long) monthlyFee);
      subscribe.setDepositSnapshot((long) depositSnapshot);

      // 로그인 유저 주입
      Member currentMember = SecurityUtil.getMemberEntity(); // 또는 상위에서 받아온 member
      subscribe.setMember(currentMember);

      // 구독 저장
      Subscribe savedSubscribe = subscribeRepository.save(subscribe);
      // 회차 테이블 생성
      subscribeRoundService.createRounds(savedSubscribe, dto);

      // 구독 히스토리 테이블 생성(매퍼에 엔티티 -> 엔티티 )
      SubscribeHistory subscribeHistory = subscribeHistoryMapper.fromSubscribe(savedSubscribe);
      subscribeHistoryRepository.save(subscribeHistory);


      return savedSubscribe;

    } catch (Exception e) {
      throw new SubscribeCreationException("구독 생성 실패");
    }
  }

  // 보증금 계산(일단 20% 고정 임시로 나중에 % 수정 가능성)
  private int calculateDeposit(int monthlyFee) {
    return (int) (monthlyFee * 0.2);
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
  @Override
  @Transactional
  public void activateAfterDelivery(Long subscribeId) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    //  // 구독 준비중
    if (subscribe.getStatus() != SubscribeStatus.PREPARING) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }

    subscribe.setStatus(SubscribeStatus.ACTIVE);
    subscribeRepository.save(subscribe);

    // 이력 기록
    historyRecorder.recordSubscribe(subscribe, ReasonCode.NONE, ActorType.SYSTEM);

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
    // 상태 변경 요청 (취소 요청)
    subscribe.setStatus(SubscribeStatus.CANCEL_REQUESTED);
    subscribeRepository.save(subscribe);

    // 상태 변경 이력 기록
    SubscribeHistory history = new SubscribeHistory();
    history.setSubscribe(subscribe);
    history.setStatus(SubscribeStatus.CANCEL_REQUESTED);
    history.setReasonCode(ReasonCode.USER_REQUEST);// 유저 요청
    subscribeHistoryRepository.save(history);
  }

  //취소 승인/거절 (관리자)
  @Override
  @Transactional
  public void processCancelRequest(Long subscribeId, boolean approved, ReasonCode reasonCode) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    if (subscribe.getStatus() != SubscribeStatus.CANCEL_REQUESTED) {
      throw new SubscribeStatusInvalidException(subscribe.getStatus().name());
    }
    if (approved) {
      // 승인 처리시
      subscribe.setStatus(SubscribeStatus.CANCELED);
      subscribeRepository.save(subscribe);
      // 기록 변경
      SubscribeHistory history = new SubscribeHistory();
      history.setSubscribe(subscribe);
      history.setStatus(SubscribeStatus.CANCELED);
      history.setReasonCode(ReasonCode.CONTRACT_CANCEL); // 승인
      subscribeHistoryRepository.save(history);
    }
    else {
      subscribe.setStatus(SubscribeStatus.ACTIVE);
      subscribeRepository.save(subscribe);

      // 기록
      SubscribeHistory history = new SubscribeHistory();
      history.setSubscribe(subscribe);
      history.setStatus(SubscribeStatus.ACTIVE);
      history.setReasonCode(ReasonCode.CANCEL_REJECTED); // 거절
      subscribeHistoryRepository.save(history);
    }
  }

  // 구독 상태 변경 이력 조회
  @Override
  @Transactional
  public List<SubscribeHistoryResponseDTO> getHistories(Long subscribeId) {
    List<SubscribeHistory> histories = subscribeHistoryRepository.findBySubscribe_Id(subscribeId);
    if(histories.isEmpty()) {
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
