package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.calculator.PurchaseRefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RentalRefundCalculator;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestAlreadyExistsException;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestStatusInvalidException;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestUnauthorizedException;
import com.imchobo.sayren_back.domain.payment.refund.mapper.RefundRequestMapper;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefundRequestServiceImpl implements RefundRequestService {

  private final RefundRequestMapper refundRequestMapper;
  private final RefundRequestRepository refundRequestRepository;
  private final PaymentRepository paymentRepository;
  private final RefundRepository refundRepository;

  private final PurchaseRefundCalculator purchaseRefundCalculator;
  private final RentalRefundCalculator rentalRefundCalculator;
  private final RefundService refundService;
  private final SubscribeRepository subscribeRepository;

  // 환불 요청 생성(멤버 정보는 시큐리티 유틸에서 멤버 아이디 가져오기)
  @Transactional
  @Override
  public RefundRequestResponseDTO createRefundRequest(RefundRequestDTO dto) {
    // 현재 로그인 멤버
    Member member = SecurityUtil.getMemberEntity();
    // 결제 정보 조회
    Payment payment = paymentRepository.findById(dto.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException(dto.getPaymentId()));

    // 이미 환불 요청이 있는지 체크
    boolean exists = refundRequestRepository.existsByOrderItemAndStatusIn(
            payment.getOrderItem(),
            List.of(RefundRequestStatus.PENDING, RefundRequestStatus.APPROVED)
    );
//    if (exists) {
//      throw new RefundRequestAlreadyExistsException(dto.getPaymentId());
//    }
    // 이미 환불 요청이 있는지 체크
//    boolean exists = refundRequestRepository.existsByOrderItemAndStatusIn(
//            payment.getOrderItem(),
//            List.of(RefundRequestStatus.PENDING, RefundRequestStatus.APPROVED)
//    );
//
// [테스트 전용 예외 처리] - exists 여도 무시하고 계속 진행
    if (exists) {
      log.warn(" 테스트 모드: 이미 환불 요청이 있지만 새로 생성합니다. paymentId={}", dto.getPaymentId());
      // 실제 운영에서는 여기서 예외 던짐
       throw new RefundRequestAlreadyExistsException(dto.getPaymentId());
    }


    // 엔티티 변환 저장
    RefundRequest entity = refundRequestMapper.toEntity(dto);
    entity.setMember(member);
    entity.setOrderItem(payment.getOrderItem());
    entity.setStatus(RefundRequestStatus.PENDING); // r기본값
    entity.setReasonCode(dto.getReasonCode()); // 기본값 세팅해둠

    RefundRequest saved = refundRequestRepository.save(entity);
    return refundRequestMapper.toResponseDTO(saved);
  }

  // 사용자 취소 (본인이 혼자 취소하는 경우)
  @Transactional
  @Override
  public void cancelRefundRequest(Long refundRequestId) {
    Member member = SecurityUtil.getMemberEntity();
    RefundRequest request = refundRequestRepository.findById(refundRequestId)
            .orElseThrow(() -> new RefundRequestNotFoundException(refundRequestId));
    // 환불 권한 여부 확인
    if (!request.getMember().getId().equals(member.getId())) {
      throw new RefundRequestUnauthorizedException();
    }
    if (request.getStatus() != RefundRequestStatus.PENDING) {
      throw new RefundRequestStatusInvalidException("이미 처리된 환불 요청");
    }
    request.setStatus(RefundRequestStatus.CANCELED);
  }

  // 결제 취소 여부
  @Transactional
  @Override
  public boolean hasActiveRefundRequest(OrderItem orderItem) {
    return refundRequestRepository.existsByOrderItemAndStatusIn(
            orderItem,
            List.of(RefundRequestStatus.PENDING, RefundRequestStatus.APPROVED)
    );
  }

  // 환불
  @Transactional
  @Override
  public RefundRequestResponseDTO processRefundRequest(Long refundRequestId, RefundRequestStatus status, ReasonCode reasonCode) {
    RefundRequest request = refundRequestRepository.findById(refundRequestId)
            .orElseThrow(() -> new RefundRequestNotFoundException(refundRequestId));
    if (request.getStatus() != RefundRequestStatus.PENDING) {
      throw new RefundRequestStatusInvalidException("이미 처리된 환불 요청");
    }

    request.setStatus(status);
    request.setReasonCode(reasonCode);

    log.info("환불 요청 처리 시작: refundRequestId={}, status={}, reasonCode={}",
            refundRequestId, status, reasonCode);

    if (status == RefundRequestStatus.APPROVED) {
      refundService.executeRefund(request, reasonCode);
    }

    log.info("환불 요청 처리 완료: refundRequestId={}, 최종상태={}",
            refundRequestId, request.getStatus());

    return refundRequestMapper.toResponseDTO(request);
  }

  // 계산 분기 처리
  private RefundCalculator getCalculator(Payment payment) {
    if (payment.getOrderItem().getOrderPlan().getType() == OrderPlanType.RENTAL) {
      return rentalRefundCalculator;
    } else { // 일반 계산
      return purchaseRefundCalculator;
    }
  }




  /// // 밑에 3개 조회 /// 나중에 처리
// 단건 조회 (본인 것만)
  @Override
  @Transactional(readOnly = true)
  public RefundRequestResponseDTO getRefundRequest(Long refundRequestId) {
    RefundRequest request = refundRequestRepository.findById(refundRequestId)
            .orElseThrow(() -> new RefundRequestNotFoundException(refundRequestId));

    Member currentMember = SecurityUtil.getMemberEntity();

    if (!Objects.equals(request.getMember().getId(), currentMember.getId())) {
      throw new RefundRequestUnauthorizedException();
    }

    return refundRequestMapper.toResponseDTO(request);
  }

  // 내 환불 요청 전체 조회
  @Override
  @Transactional(readOnly = true)
  public List<RefundRequestResponseDTO> getMyRefundRequests() {
    Member currentMember = SecurityUtil.getMemberEntity();

    List<RefundRequest> requests = refundRequestRepository
            .findByMemberOrderByRegDateDesc(currentMember);

    return refundRequestMapper.toResponseDTOs(requests);
  }

  // 관리자: 특정 회원 환불 요청 조회
  @Override
  @Transactional(readOnly = true)
  public List<RefundRequestResponseDTO> getAllRefundRequests() {
    List<RefundRequest> requests = refundRequestRepository.findAllByOrderByRegDateDesc();
    List<RefundRequestResponseDTO> dtos = refundRequestMapper.toResponseDTOs(requests);

    for (int i = 0; i < requests.size(); i++) {
      RefundRequest req = requests.get(i);
      RefundRequestResponseDTO dto = dtos.get(i);

      List<Payment> payments = paymentRepository.findByOrderItem(req.getOrderItem());
      if (!payments.isEmpty()) {
        Payment latestPayment = payments.get(payments.size() - 1); // 최근 결제
        dto.setPaymentId(latestPayment.getId());
      }
    }
    return dtos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<RefundRequestResponseDTO> getRefundRequestsByMember(Long memberId) {
    List<RefundRequest> requests = refundRequestRepository.findByMember_Id(memberId);
    List<RefundRequestResponseDTO> dtos = refundRequestMapper.toResponseDTOs(requests);

    for (int i = 0; i < requests.size(); i++) {
      RefundRequest req = requests.get(i);
      RefundRequestResponseDTO dto = dtos.get(i);

      List<Payment> payments = paymentRepository.findByOrderItem(req.getOrderItem());
      if (!payments.isEmpty()) {
        Payment latestPayment = payments.get(payments.size() - 1);
        dto.setPaymentId(latestPayment.getId());
      }
    }
    return dtos;
  }


}
