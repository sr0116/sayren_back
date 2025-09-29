package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.calculator.PurchaseRefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RentalRefundCalculator;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestStatusInvalidException;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundRequestUnauthorizedException;
import com.imchobo.sayren_back.domain.payment.refund.mapper.RefundRequestMapper;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefundRequestServiceImpl implements RefundRequestService{

  private final RefundRequestMapper refundRequestMapper;
  private final RefundRequestRepository refundRequestRepository;
  private final PaymentRepository paymentRepository;
  private final RefundRepository refundRepository;

  private final PurchaseRefundCalculator purchaseRefundCalculator;
  private final RentalRefundCalculator rentalRefundCalculator;

  // 환불 요청 생성(멤버 정보는 시큐리티 유틸에서 멤버 아이디 가져오기)
  @Override
  public RefundRequestResponseDTO createRefundRequest(RefundRequestDTO dto) {
    Member member = SecurityUtil.getMemberEntity();

    RefundRequest entity = refundRequestMapper.toEntity(dto);
    entity.setMember(member); // 로그인 멤버 주입
    entity.setStatus(RefundRequestStatus.PENDING); // 요청 대기 상태

    RefundRequest saved = refundRequestRepository.save(entity);
    return refundRequestMapper.toResponseDTO(saved);
  }

  // 사용자 취소 (본인이 혼자 취소하는 경우)
  @Override
  public void cancelRefundRequest(Long refundRequestId) {
    Member member = SecurityUtil.getMemberEntity();
    RefundRequest request = refundRequestRepository.findById(refundRequestId)
            .orElseThrow(() -> new RefundRequestNotFoundException(refundRequestId));
    // 환불 권한 여부 확인
    if (!request.getMember().getId().equals(member.getId())) {
      throw  new RefundRequestUnauthorizedException();
    }
    if (request.getStatus() != RefundRequestStatus.PENDING){
      throw  new RefundRequestStatusInvalidException("이미 처리된 환불 요청");
    }
    request.setStatus(RefundRequestStatus.CANCELED);
  }

  // 관리자 승인/ 거절 여부
  @Override
  public RefundRequestResponseDTO processRefundRequest(Long refundRequestId, RefundRequestStatus status, ReasonCode reasonCode) {
    RefundRequest request = refundRequestRepository.findById(refundRequestId)
            .orElseThrow(() -> new RefundRequestNotFoundException(refundRequestId));
    if (request.getStatus() != RefundRequestStatus.PENDING){
      throw new RefundRequestStatusInvalidException("이미 처리된 환불 요청");
    }

    request.setStatus(status);
    request.setReasonCode(reasonCode);

    // 승인시 추가 처리
    if(status == RefundRequestStatus.APPROVED){
      Payment payment = paymentRepository.findByOrderItem(request.getOrderItem())
              .orElseThrow(() -> new PaymentNotFoundException(
                      request.getOrderItem().getId()));

      RefundCalculator calculator = getCalculator(payment);
      Long refundAmount = calculator.calculateRefundAmount(payment, request);

      // refund 엔티티 저장
      Refund refund = Refund.builder()
              .payment(payment)
              .refundRequest(request)
              .amount(refundAmount)
              .reasonCode(reasonCode)
              .build();
      refundRepository.save(refund);

      // payment 상태 변경(일단 배송 고려 안하고 환불 바로)
      payment.setPaymentStatus(PaymentStatus.REFUNDED);
    }
    return refundRequestMapper.toResponseDTO(request);
  }

  // 계산 분기 처리
  private RefundCalculator getCalculator(Payment payment) {
    if (payment.getOrderItem().getOrderPlan().getType() == OrderPlanType.RENTAL){
      return rentalRefundCalculator;
    } else { // 일반 계산
      return purchaseRefundCalculator;
    }
  }
/// // 밑에 3개 조회 /// 나중에 처리
  @Override
  public RefundRequestResponseDTO getRefundRequest(Long refundRequestId) {
    return null;
  }

  @Override
  public List<RefundRequestResponseDTO> getMyRefundRequests() {
    return List.of();
  }

  @Override
  public List<RefundRequestResponseDTO> getRefundRequestsByMember(Long memberId) {
    return List.of();
  }
}
